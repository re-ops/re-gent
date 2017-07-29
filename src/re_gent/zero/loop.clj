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

(def read-flag (atom true))

(def t (atom nil))

(defn- read-loop [dealer]
  (let [items (into-array [(ZMQ$PollItem. dealer ZMQ$Poller/POLLIN)])]
    (info "setting up read loop")
    (while @read-flag
      (try
        (ZMQ/poll items 10)
        (when (.isReadable (aget items 0))
          (handle-message (.recv dealer 0)))
        (catch Exception e
          (error e (.getMessage e) (.getStackTrace e)))))
    (info "read loop stopped")))

(defn setup-loop [dealer]
  (reset! read-flag true)
  (reset! t (future (read-loop dealer))))

(defn stop-loop! []
  (reset! read-flag false)
  (when @t (future-cancel @t)))
