(ns re-gent.zero.client
  "Zeromq dealer client"
  (:require
   [re-share.zero.keys :refer (create-client-keys client-keys-exist?)]
   [clojure.core.strint :refer (<<)]
   [taoensso.timbre :refer (refer-timbre)]
   [taoensso.nippy :as nippy :refer (freeze thaw)]
   [taoensso.timbre :refer  (refer-timbre)]
   [re-share.zero.common :refer (client-socket)])
  (:import
   [org.zeromq ZMQ]
   [java.net InetAddress]))

(refer-timbre)

(defn hostname []
  (let [addr (. InetAddress getLocalHost)]
    (.getHostName addr)))

(defn dealer-socket [ctx host port parent]
  (let [uid (format "%04X-%04X" (rand-int 30) (rand-int 30))
        id (freeze {:hostname (hostname) :uid uid})]
    (info "uid" uid)
    (doto (client-socket ctx ZMQ/DEALER parent)
      (.setIdentity id)
      (.connect (<< "tcp://~{host}:~{port}")))))

(def socket (atom nil))

(defn send- [m]
  (debug "sending" m)
  (when @socket
    (.send @socket (freeze m) 0)))

(defn- monitor [socket]
  (.monitor socket "inproc://events" ZMQ/EVENT_ALL))

(defn setup-client [ctx host port parent]
  (create-client-keys ".curve")
  (when-not (client-keys-exist? parent)
    (throw (ex-info "server public key is missing!" {:parent parent :host host})))
  (reset! socket (dealer-socket ctx host port parent))
  (monitor @socket)
  @socket)

(defn stop-client! []
  (when @socket
    (.setLinger @socket 0)
    (.close @socket)
    (reset! socket nil)))

(comment
  (setup-client "127.0.0.1" 9090 ".curve")
  (stop-client!)
  (println socket))
