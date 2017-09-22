(ns re-gent.zero.events
  (:require
   [taoensso.timbre :refer (refer-timbre)])
  (:import
   [org.zeromq ZMQ ZMQ$Event ZMQ$Poller]))

(refer-timbre)

(def t (atom nil))
(def flag (atom true))

(def types
  {ZMQ/EVENT_CONNECTED :connected
   ZMQ/EVENT_CONNECT_DELAYED :delayed
   ZMQ/EVENT_DISCONNECTED :disconnected
   ZMQ/EVENT_CLOSED :closed
   ZMQ/EVENT_CONNECT_RETRIED :retried})

(defn events-loop [ctx]
  (let [socket (.socket ctx ZMQ/PAIR)]
    (try
      (.connect socket "inproc://events")
      (info "events loop running")
      (while @flag
        (let [event (ZMQ$Event/recv socket 0)]
          (when event
            (debug (get types (.getEvent event) "Nan Event")))))
      (catch Exception e
        (error (.getMessage e)))
      (finally
        (.setLinger socket 0)
        (.close socket)
        (info "events loop is down")))))

(defn setup-events [ctx]
  (reset! flag true)
  (reset! t (future (events-loop ctx))))

(defn stop-events! []
  (reset! flag false)
  (when @t
    (reset! t nil)))
