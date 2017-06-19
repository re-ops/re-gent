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

(ns re-gent.metrics
  (:require
    [taoensso.timbre :refer (refer-timbre set-level! merge-config!)]
    [cheshire.core :refer (parse-string)])
  (:import
     [oshi.json hardware.CentralProcessor SystemInfo util.PropertiesUtil]))

(refer-timbre)

(def si (SystemInfo.))

(def hal (.getHardware si))

(defn read-metrics
  ([]
    (read-metrics (PropertiesUtil/loadProperties "oshi.json.properties")))
  ([props]
    (parse-string (.toCompactJSON si props) true)))

(comment
  (clojure.pprint/pprint (get-in (read-metrics) [:operatingSystem :processes]))
  (clojure.pprint/pprint (bean (.getComputerSystem hal)))
  (bean (.getProcessor hal)))
