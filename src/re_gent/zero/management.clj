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

(ns re-gent.zero.management
  "Client registration/processing"
  (:require
    [serializable.fn :as s]
    [clojure.core.match :refer  [match]]
    [taoensso.timbre :refer (refer-timbre)]
    [re-gent.metrics :refer (read-metrics)]
    [re-gent.zero.client :refer (send-)]))

(refer-timbre)

(defn process
   "process server requests"
   [request]
  (debug "processing..")
  (match [request]
    [{:request :metrics}] (send- {:reply :metrics :content (read-metrics)})
    [{:request :execute :fn f}] (info f) #_(send- {:reply :metrics :content (read-metrics)})
    [{:response :ok :on {:request :register}}] (info "registered successfuly")
    :else (info "no handler found for" request)
    )
  )

(defn register []
  (send- {:request :register}))

(defn unregister []
  (send- {:request :unregister}))
