(ns re-gent.zero.functions
  (:require
   [re-share.oshi :refer (read-metrics os get-processes)]
   [re-scan.core :refer [nmap into-ports into-hosts]]
   [clojure.java.shell :refer [sh]]
   [cheshire.core :refer :all]
   [serializable.fn :refer :all]
   [me.raynes.fs :refer :all]))

(comment
  (clojure.pprint/pprint (read-metrics)))
