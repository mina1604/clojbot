(ns clojbot.facebook.auth
  (:gen-class)
  (:require [environ.core :refer [env]]
            [taoensso.timbre :as timbre]))

(def ^:private verify-token (env :verify-token))
(def ^:private page-secret (env :page-secret))

(defn authenticate [request]
  (timbre/debug "Authenticating incoming webhook verification request" request)
  (let [params (:params request)]
    (if (and (= (params "hub.mode") "subscribe")
             (= (params "hub.verify_token") verify-token))
        {:status 200 :body (params "hub.challenge")}
        {:status 403})))
