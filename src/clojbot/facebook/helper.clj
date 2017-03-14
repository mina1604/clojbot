(ns clojbot.facebook.helper
  (:gen-class))

(defn image-message [image-url]
  {:attachment {:type "image"
                :payload {:url image-url}}})

(defn video-message [video-url]
  {:attachment {:type "video"
                :payload {:url video-url}}})

(defn audio-message [audio-url]
  {:attachment {:type "audio"
                :payload {:url audio-url}}})

(defn file-message [file-url]
  {:attachment {:type "file"
                :payload {:url file-url}}})

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
