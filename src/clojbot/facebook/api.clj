(ns clojbot.facebook.api
  (:gen-class)
  (:require [clojure.data.json :as json]
            [clojure.string :as s]
            [environ.core :refer [env]]
            [org.httpkit.client :as http]
            [taoensso.timbre :as timbre]))

(def ^:private api-version "v2.6")
(def ^:private api-url "https://graph.facebook.com/")

(def ^:private page-access-token (env :page-access-token))

(defn- handle-facebook-response [response]
  (when-not (= (:status response) 200)
    (timbre/warn "Facebook error response" (:body response))
    (throw (Exception. (str "POST Send API" (:body response)))))
  #_(timbre/debug "Facebook response" (:body response))
  (json/read-str (:body response) :key-fn keyword))

(defn- post-api [endpoint body]
  (timbre/debug "POST Send API" endpoint body)
  (let [response @(http/post (str api-url api-version "/" endpoint)
                             {:query-params {"access_token" page-access-token}
                              :headers {"Content-Type" "application/json"}
                              :body (json/write-str body)
                              :insecure? true})]
    (handle-facebook-response response)))


(defn send-message
  "sends Message to PSID via FB Send Api, see:
   https://developers.facebook.com/docs/messenger-platform/send-api-reference"
  [psid message]
  (post-api "me/messages" {:recipient {:id psid}
                           :message message}))

(defn send-sender-action
  "send Sender Action to PSID via FB Send Api, see:
   https://developers.facebook.com/docs/messenger-platform/send-api-reference/sender-actions"
  [psid sender-action]
  (post-api "me/messages" {:recipient {:id psid}
                           :sender_action sender-action}))

(defn upload-attachment
  "uploads Attachment via FB Upload API and returns body containing :attachment_id, see:
   https://developers.facebook.com/docs/messenger-platform/send-api-reference/attachment-upload/v2.8"
  [{type :type
    url  :url}]
  (post-api "me/message_attachments"
            {:message {:attachment {:type type
                                    :payload {:url url
                                              :is_reusable true}}}}))

(defn set-messenger-profile
  "sets messenger profile data, see:
   https://developers.facebook.com/docs/messenger-platform/messenger-profile"
  [profile]
  (post-api "me/messenger_profile" profile))


(defn get-user-profile
  "gets user profile data for PSID, see:
   https://developers.facebook.com/docs/messenger-platform/user-profile"
  [psid]
  (let [response @(http/get (str api-url api-version "/" psid)
                           {:query-params {:access_token page-access-token
                                           :fields "first_name,last_name,profile_pic,locale,timezone,gender"}
                            :headers {"Content-Type" "application/json"}
                            :insecure? true})]
    (handle-facebook-response response)))
