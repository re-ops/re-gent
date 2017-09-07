(ns re-gent.zero.client
  "Zeromq dealer client"
  (:require
   [re-share.zero.keys :refer (create-client-keys client-keys-exist?)]
   [clojure.core.strint :refer (<<)]
   [taoensso.timbre :refer (refer-timbre)]
   [taoensso.nippy :as nippy :refer (freeze thaw)]
   [taoensso.timbre :refer  (refer-timbre)]
   [re-share.zero.common :refer (client-socket context close!)])
  (:import
   [org.zeromq ZMQ]
   [java.net InetAddress]))

(refer-timbre)

(defn hostname []
  (let [addr (. InetAddress getLocalHost)]
    (.getHostName addr)))

(def ctx (atom nil))

(defn dealer-socket [host port parent]
  (let [uid (format "%04X-%04X" (rand-int 30) (rand-int 30))
        id (freeze {:hostname (hostname) :uid uid})]
    (info "uid" uid)
    (doto (client-socket @ctx ZMQ/DEALER parent)
      (.setIdentity id)
      (.connect (<< "tcp://~{host}:~{port}")))))

(def sockets (atom {}))

(defn send- [m]
  (let [{:keys [dealer]} @sockets]
    (.send dealer (freeze m) 0)))

(defn setup-client [host port parent]
  (create-client-keys ".curve")
  (when-not (client-keys-exist? parent)
    (throw (ex-info "server public key is missing!" {:parent parent :host host})))
  (reset! ctx (context))
  (reset! sockets {:dealer (dealer-socket host port parent)})
  [(@sockets :dealer) @ctx])

(defn stop-client! []
  (close! @sockets)
  (when @ctx
    (.term @ctx))
  (reset! ctx nil))

(comment
  (setup-client "127.0.0.1" 9090 ".curve")
  (stop-client!)
  (println @sockets))
