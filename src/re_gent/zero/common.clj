(ns re-gent.zero.common
  (:require
   [clojure.core.strint :refer  (<<)]
   [re-gent.zero.keys :refer (read-key paths)])
  (:import
   [org.zeromq ZMQ]))

(defn context [] (ZMQ/context 1))

(defn close! [sockets]
  (doseq [[k s] sockets] (.close s)))

(defn client-socket [t parent]
  (let [{:keys [server-public client-public client-private]} (paths parent)]
    (doto
     (.socket (context) t)
      (.setZAPDomain (.getBytes "global"))
      (.setCurveServerKey (read-key server-public))
      (.setCurvePublicKey (read-key client-public))
      (.setCurveSecretKey (read-key client-private)))))

(comment
  (read-key ".curve/client-private.key")
  (alength (read-key ".curve/client-public.key")))
