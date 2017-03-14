(ns clojbot.message-pipe
  "processes incoming information/messages via multiple stages
    understand: tries to understand input (extracts meaning out of text, attachments)
    enrich: tries to enrich information (mapping intent to reaction, processing attachments)
    decide: decides if sufficient information exists to generate replies (nlp confidence, attachment data)
    reply: sends out replies"
  (:gen-class)
  (:require [clojure.string :as str]
            [taoensso.timbre :as timbre]
            [clojbot.reactions :as reactions]
            [clojbot.facebook.api :as fb-api]))

(def intent->reaction {:get-started reactions/greet
                       :get-help    reactions/help
                       :foo         reactions/fallback})

(defn- apply-rules [information]
  (if-let [text (:text information)]
    (assoc information
           :intent
           (cond
             (re-matches #"hi|hello|hallo|" text) :get-started
             (re-matches #"help" text)            :get-help
             :else                                :foo))
    information))

(defn- map-intent [information]
  (if-let [intent (:intent information)]
    (assoc information
           :reaction
           (intent intent->reaction))
    information))

(defn- generate-replies [information]
  (if-let [reaction (:reaction information)]
    (assoc information
           :replies
           (reaction))
    information))

(defn understand [information]
  (timbre/debug "understanding" information)
  (-> (apply-rules information)))

(defn enrich [information]
  (timbre/debug "enriching" information)
  (-> (map-intent information)))

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
