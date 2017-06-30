(comment
  re-gent, Copyright 2017 Ronen Narkis, narkisr.com
  Licensed under the Apache License,
  Version 2.0  (the "License") you may not use this file except in compliance with the License.
  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.)

(ns re-gent.zero.common
  (:require
    [clojure.core.strint :refer  (<<)]
    [re-gent.zero.keys :refer (read-key paths)])
  (:import
    [org.zeromq ZMQ]
    ))

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
  (alength (read-key ".curve/client-public.key")) 
  )
