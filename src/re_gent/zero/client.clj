(ns re-gent.zero.client
  "Zeromq dealer client"
  (:require
     [re-gent.zero.keys :refer (create-keys server-key-exist?)]
     [clojure.core.strint :refer (<<)]
     [taoensso.timbre :refer (refer-timbre)]
     [taoensso.nippy :as nippy :refer (freeze thaw)]
     [re-gent.zero.common :refer (client-socket context close!)])
  (:import
     [org.zeromq ZMQ]
     [java.net InetAddress]))

(defn hostname []
  (let [addr (. InetAddress getLocalHost)]
    (.getHostName addr)))

(defn dealer-socket [host port parent]
  (let [id (freeze {:hostname (hostname) :uid (format "%04X-%04X" (rand-int 30) (rand-int 30))})]
    (doto (client-socket ZMQ/DEALER parent)
      (.setIdentity id)
      (.connect (<< "tcp://~{host}:~{port}")))))

(def sockets (atom {}))

(defn send- [m]
  (let [{:keys [dealer]} @sockets]
    (.send dealer (freeze m) 0)))

(defn setup-client [host port parent]
  (create-keys ".curve")
  (when-not (server-key-exist? parent)
    (throw (ex-info "server public key is missing!" {:parent parent :host host})))
  (reset! sockets {:dealer (dealer-socket host port parent)})
  (@sockets :dealer))

(defn stop-client! []
  (close! @sockets))

(comment
  (setup-client "127.0.0.1" 9090 ".curve")
  (stop-client!)
  (println @sockets)
  )
