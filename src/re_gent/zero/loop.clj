(ns re-gent.zero.loop
  (:require
   [taoensso.nippy :as nippy :refer (thaw)]
   [taoensso.timbre :refer (refer-timbre)]
   [re-gent.zero.reply :refer (setup-reply reset-reply! peek-send)]
   [re-gent.zero.management :refer (process)])
  (:import
   [org.zeromq ZMsg ZMQ ZMQ$PollItem ZMQ$Poller]))

(refer-timbre)

(defn- handle-message [message]
  (future (process message)))

(def flag (atom true))

(defn error-m [e]
  (error e (.getMessage e) (.getStackTrace e)))

(defn- socket-loop [dealer]
  (try
    (info "setting up read loop")
    (while @flag
      (if-let [msg (ZMsg/recvMsg dealer ZMQ/DONTWAIT)]
        (when-let [content (.pop msg)]
          (handle-message (thaw (.getData content))))
        (or (peek-send dealer) (Thread/sleep 10))))
    (catch Exception e
      (error-m e)))
  (info "read loop stopped"))

(defn setup-loop [dealer]
  (setup-reply)
  (reset! flag true)
  (future (socket-loop dealer)))

(defn stop-loop! []
  (reset! flag false)
  (reset-reply!))
