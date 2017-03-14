(ns clojbot.core
  (:gen-class)
  (:require [clojure.core.async :refer [go]]
            [compojure.core :refer :all]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.json :refer [wrap-json-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [taoensso.timbre :as timbre]
            [clojbot.bot :as bot]
            [clojbot.facebook.auth :as fb-auth]))

(defn config-timbre! []
  (let [colors {:debug :cyan :info :green, :warn :yellow, :error :red, :fatal :purple, :report :blue}]
    (timbre/set-config!
      {:level :debug
       ;;:ns-whitelist ["clojbot.facebook.auth"]
       :ns-blacklist ["clojbot.message-pipe"]
       :appenders
       {:color-appender
         {:enabled?   true
          :async?     false
          :min-level  nil
          :rate-limit nil
          :output-fn  :inherit
          :fn (fn [{:keys [error? level output-fn] :as data}]
                (binding [*out* (if error? *err* *out*)]
                  (if-let [color (colors level)]
                    (println (timbre/color-str color (output-fn data)))
                    (println (output-fn data)))))}}})))

(config-timbre!)

(defroutes fb-routes
  (POST "/webhook" request
    (try
      (bot/handle-webhook-request request)
      (catch Exception e))
    {:status 200})

  (GET "/webhook" request
    (fb-auth/authenticate request)))

(def app
  ;; TODO: implement wrap-json-params replacement:
  ;;       custom middleware that implements
  ;;       https://developers.facebook.com/docs/messenger-platform/webhook-reference#security
  ;;       and extracts response body as json (sd)
  (wrap-json-params (wrap-defaults fb-routes api-defaults)))


