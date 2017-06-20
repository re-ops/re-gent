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

(def read-flag (atom true))
(def t (atom nil))

(defn- read-loop [dealer]
  (let [items (into-array [(ZMQ$PollItem. dealer ZMQ$Poller/POLLIN)]) ]
    (info "setting up read loop")
    (while @read-flag
      (try 
         (ZMQ/poll items 10)
         (when (.isReadable (aget items 0))
           (handle-message (.recv dealer 0)))
         (catch Exception e 
           (error e (.getMessage e) (.getStackTrace e)))))
    (info "read loop stopped")))

(defn setup-loop [dealer]
  (reset! read-flag true)
  (reset! t (future (read-loop dealer))))

(defn stop-loop! []
  (reset! read-flag false)
  (future-cancel @t))
