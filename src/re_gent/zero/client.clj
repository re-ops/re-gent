(ns re-gent.zero.client
  "Zeromq dealer client"
  (:require
     [clojure.core.strint :refer (<<)]
     [taoensso.timbre :refer (refer-timbre)]
     [taoensso.nippy :as nippy :refer (freeze thaw)]
     [re-gent.zero.common :refer (read-key client-socket context close!)])
  (:import
     [org.zeromq ZMQ]
     [java.net InetAddress]))

(refer-timbre)

(defn hostname []
  (let [addr (. InetAddress getLocalHost)]
    (.getHostName addr)))

(defn dealer-socket [host parent]
  (let [id (freeze {:hostname (hostname) :uid (format "%04X-%04X" (rand-int 30) (rand-int 30))})]
    (doto (client-socket ZMQ/DEALER parent)
      (.setIdentity id)
      (.connect (<< "tcp://~{host}:9000")))))

(def sockets (atom {}))

(defn send- [m]
  (let [{:keys [dealer]} @sockets]
    (.send dealer (freeze m) 0)))

(defn setup-client [host parent]
  (reset! sockets {:dealer (dealer-socket host parent)})
  (@sockets :dealer))

(defn stop-client! []
  (close! @sockets))

(comment
  (setup-client "127.0.0.1" ".curve")
  (stop-client!)
  (println @sockets)
  )
