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

(defn- read-loop [dealer ctx]
  (let [items (into-array [(ZMQ$PollItem. dealer ZMQ$Poller/POLLIN)]) selector (.selector ctx) ]
    (try
       (info "setting up read loop")
       (while (not (Thread/interrupted))
         (try
           (ZMQ/poll selector items 10)
           (when (.isReadable (aget items 0))
             (handle-message (.recv dealer 0)))
         (catch Exception e
           (warn e))))
    (finally
      (.close selector)
      (.setLinger 0)
      (.close dealer)))
    (info "read loop stopped")))

(defn setup-loop [dealer ctx]
  (reset! t (future (read-loop dealer ctx))))

(defn stop-loop! []
  (when @t (future-cancel @t)))
