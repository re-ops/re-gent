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
