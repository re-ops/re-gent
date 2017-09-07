(ns re-gent.zero.loop
  (:require
   [re-share.zero.common :refer (context)]
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

(defn error-m [e]
  (error e (.getMessage e) (.getStackTrace e)))

(defn- read-loop [dealer ctx]
  (let [items (into-array [(ZMQ$PollItem. dealer ZMQ$Poller/POLLIN)])]
    (try 
      (let [selector (.selector ctx)]
        (info "setting up read loop")
        (while @read-flag
          (try
            (ZMQ/poll selector items 10)
            (when (.isReadable (aget items 0))
              (handle-message (.recv dealer 0)))
            (catch Exception e
              (error-m e)))))
      (catch Exception e 
        (error-m e)))

    (info "read loop stopped")))

(defn setup-loop [dealer ctx]
  (reset! read-flag true)
  (reset! t (future (read-loop dealer ctx))))

(defn stop-loop! []
  (reset! read-flag false)
  (when @t (future-cancel @t)))
