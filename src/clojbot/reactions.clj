(ns clojbot.reactions
  (:gen-class)
  (:require [clojbot.facebook.helper :as helper]))

(defn greet
  "generates greeting messages"
  []
  [(helper/text-message "Hello & Welcome")])

(defn fallback
  "generates fallback messages"
  []
  [(helper/text-message "Whut is this?!")])

(defn help
  "generates help messages"
  []
  [(helper/text-message "Sorry, can't help you!")])
