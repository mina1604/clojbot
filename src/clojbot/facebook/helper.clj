(ns clojbot.facebook.helper
  (:gen-class))

(defn image-message [image-url]
  {:attachment {:type "image"
                :payload {:url image-url}}})

(defn text-message [text]
  {:text text})

(defn quick-replies-message [text quick-replies]
  {:text text
   :quick_replies quick-replies})

(defn button-message [text buttons]
  {:attachment {:type "template"
                :payload { :template_type "button"
                           :text text
                           :buttons buttons}}})
