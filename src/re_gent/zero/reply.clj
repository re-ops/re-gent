(ns re-gent.zero.reply
  (:require
   [taoensso.timbre :refer (refer-timbre)]
   [taoensso.nippy :as nippy :refer (freeze)]))

(refer-timbre)

(def send-queue (atom (clojure.lang.PersistentQueue/EMPTY)))

(defn peek-send [socket]
  (when-let [m (peek @send-queue)]
    (debug "sending" m)
    (assert (= (.send socket (freeze m) 0) true))
    (swap! send-queue pop)
    (debug "sent" m)))

(defn send- [m]
  (swap! send-queue conj m))

(defn setup-reply []
  (reset! send-queue (clojure.lang.PersistentQueue/EMPTY)))

(defn reset-reply! []
  (reset! send-queue nil))
