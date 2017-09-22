(ns re-gent.zero.loop
  (:require
   [taoensso.nippy :as nippy :refer (thaw)]
   [taoensso.timbre :refer (refer-timbre)]
   [re-gent.zero.management :refer (process)])
  (:import
   [org.zeromq ZMsg ZMQ ZMQ$PollItem ZMQ$Poller]))

(refer-timbre)

(defn- handle-message [message]
  (debug "processing")
  (process (thaw message)))

(def t (atom nil))
(def flag (atom true))

(defn error-m [e]
  (error e (.getMessage e) (.getStackTrace e)))

(defn- read-loop [dealer]
  (try
    (info "setting up read loop")
    (while @flag
      (let [msg (ZMsg/recvMsg dealer) content (.pop msg)]
        (when content
          (handle-message (.getData content)))))
    (catch Exception e
      (error-m e)))
  (info "read loop stopped"))

(defn setup-loop [dealer]
  (reset! t (future (read-loop dealer))))

(defn stop-loop! []
  (reset! flag false)
  (when @t 
    (reset! t nil)))
