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

(defn error-m [e]
  (error e (.getMessage e) (.getStackTrace e)))

(defn- read-loop [dealer selector]
  (let [items (into-array [(ZMQ$PollItem. dealer ZMQ$Poller/POLLIN)])]
    (try
       (info "setting up read loop")
       (while (not (Thread/interrupted))
         #_(try
           (ZMQ/poll selector items 10)
           (when (.isReadable (aget items 0))
             (handle-message (.recv dealer 0)))
         (catch java.nio.channels.ClosedChannelException e 
           (throw e))
         (catch Exception e
           (error-m e))))
    (finally
      (.close selector)
      (.setLinger 0)
      (.close dealer)))
    (info "read loop stopped")))

(defn setup-loop [dealer selector]
  (reset! t (future (read-loop dealer selector))))

(defn stop-loop! []
  (when @t (future-cancel @t)))
