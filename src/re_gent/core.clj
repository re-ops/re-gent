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

(ns re-gent.core
  (:require 
    [re-gent.zero.client :refer (setup-client stop-client!)]
    [re-gent.zero.loop :refer (setup-loop stop-loop!)]
    [re-gent.zero.management :refer (register unregister)]
    [re-gent.log :refer (setup-logging)]))

(defn setup []
  (setup-logging)
  (let [dealer (setup-client "127.0.0.1" ".curve")] 
    (setup-loop dealer))
    (register))


(defn stop!  []
  (unregister)
  (stop-loop!)
  (stop-client!)
  )