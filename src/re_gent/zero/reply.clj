(ns re-gent.zero.reply
  (:require
   [taoensso.timbre :refer (refer-timbre)]
   [taoensso.nippy :as nippy :refer (freeze)]))

(refer-timbre)

(def send-queue (atom (clojure.lang.PersistentQueue/EMPTY)))

(defn safe-freeze
  "Trying to freeze m failing without killing the agent"
  [m]
  (try
    (freeze m)
    (catch Exception e
      (error e))))

(defn peek-send [socket]
  (when-let [m (peek @send-queue)]
    (when-let [ice (safe-freeze m)]
      (debug "sending" m)
      (assert (= (.send socket ice 0) true))
      (swap! send-queue pop)
      (debug "sent" m)
      true)))

(defn send- [m]
  (swap! send-queue conj m))

(defn setup-reply []
  (reset! send-queue (clojure.lang.PersistentQueue/EMPTY)))

(defn reset-reply! []
  (reset! send-queue nil))
