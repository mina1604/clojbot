(ns clojbot.bot
  (:gen-class)
  (:require [clojure.string :as str]
            [taoensso.timbre :as timbre]
            [clojbot.message-pipe :refer :all]
            [clojbot.facebook.api :as fb-api]))

(def messenger-profile
     {:get_started {:payload "get-started"}
      :persistent_menu [{:locale "default"
                         :call_to_actions [{:title "Help"
                                            :type "postback"
                                            :payload "get-help"}]}]})

(try
  (fb-api/set-messenger-profile messenger-profile)
  (catch Exception e))

(defn on-postback
  "processes postback"
  [{sender    :sender
    postback  :postback
    recipient :recipient
    timestamp :timestamp}]
  (let [information {:sender sender
                     :intent (keyword (:payload postback))}]
    (-> (enrich information)
      decide
      reply)))

(defn on-quick-reply
  "processes quick-reply"
  [{sender      :sender
    quick-reply :quick_reply
    recipient   :recipient
    timestamp   :timestamp}]
  (let [information {:sender sender
                     :intent (:payload quick-reply)}]
    (-> (enrich information)
      decide
      reply)))

(defn on-text
  "processes text"
  [{sender       :sender
    {text :text} :message
    recipient    :recipient
    timestamp    :timestamp}]
  (let [information {:sender sender
                     :text (str/lower-case text)}]
    (-> (understand information)
      enrich
      decide
      reply)))

(defn on-message
  "processes message"
  [message-event]
  (let [message (:message message-event)]
    ((cond
       ; (contains? message :attachment) on-attachments
       (contains? message :quick-reply) on-quick-reply
       (contains? message :text) on-text) message-event)))

(defn handle-message-event [message-event]
  ((cond
     (contains? message-event :postback) on-postback
     (contains? message-event :message)  on-message) message-event))

(defn handle-webhook-request
  "processes incoming facebook webhook events"
  [request]
  (let [data (:params request)
        object (:object data)]
    (when (= object "page")
      (doseq [entry (:entry data)]
        (doseq [messaging-event (:messaging entry)]
          (handle-message-event messaging-event))))))
