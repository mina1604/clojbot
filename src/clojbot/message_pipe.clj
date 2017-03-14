(ns clojbot.message-pipe
  (:gen-class)
  (:require [clojure.string :as str]
            [taoensso.timbre :as timbre]
            [clojbot.reactions :as reactions]
            [clojbot.facebook.api :as fb-api]))

(def intent->reaction {:get-started reactions/greet
                       :get-help reactions/help
                       :foo reactions/fallback})

(defn- apply-rules [information]
  (assoc information
         :intent
         (cond
           (re-matches #"hi|hello|hallo|" (:text information)) :get-started
           (re-matches #"help" (:text information)) :get-help
           :else :foo)))

(defn understand [information]
  (timbre/debug "understanding" information)
  (-> (apply-rules information)))

(defn- map-intent [information]
  (assoc information
         :reaction
         ((:intent information) intent->reaction)))

(defn enrich [information]
  (timbre/debug "enriching" information)
  (-> (map-intent information)))


(defn- generate-replies [information]
  (assoc information
         :replies
         ((:reaction information))))

(defn decide [information]
  (timbre/debug "deciding" information)
  (-> (generate-replies information)))

(defn reply [information]
  (timbre/debug "replying" information)
  (let [psid (get-in information [:sender :id])]
    (doseq [reply (:replies information)]
      (try
        (fb-api/send-message psid reply)
        (catch Exception e)))))
