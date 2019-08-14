(ns re-gent.zero.reply
  (:require
   [cheshire.core :refer (generate-string)]
   [clojure.data.codec.base64 :as b64]
   [taoensso.timbre :refer (refer-timbre)]
   [taoensso.nippy :as nippy :refer (freeze)]))

(refer-timbre)

(def send-queue (atom (clojure.lang.PersistentQueue/EMPTY)))

(defn into-base64 [original]
  (b64/encode (.getBytes original)))

(defn safe-encode
  "Trying to encode m without killing the agent on failure"
  [m]
  (try
    (into-base64 (generate-string m))
    (catch Exception e
      (error "Failed to encode" e))))

(defn peek-send [socket]
  (when-let [m (peek @send-queue)]
    (when-let [encoded (safe-encode m)]
      (debug "sending" m)
      (assert (= (.send socket encoded 0) true))
      (swap! send-queue pop)
      (debug "sent" m)
      true)))

(defn send- [m]
  (swap! send-queue conj m))

(defn setup-reply []
  (reset! send-queue (clojure.lang.PersistentQueue/EMPTY)))

(defn reset-reply! []
  (reset! send-queue nil))
