(ns re-gent.core
  (:gen-class)
  (:require
   [taoensso.timbre :refer (refer-timbre)]
   [clojure.core.strint :refer (<<)]
   [re-gent.zero.client :refer (setup-client stop-client!)]
   [re-gent.zero.loop :refer (setup-loop stop-loop!)]
   [re-gent.zero.management :refer (register unregister)]
   [re-gent.log :refer (setup-logging)]))

(refer-timbre)

(def version "0.1.0")

(defn stop
  "Stop the loop and unregister"
  [_]
  (warn "shutting down!")
  (unregister)
  (stop-loop!)
  (stop-client!))

(defn add-shutdown []
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop)))

(defn setup
  ([] (setup "127.0.0.1" "9000"))
  ([host port]
   (setup-logging)
   (add-shutdown)
   (setup-client host port ".curve")))

(defn start
   "start this re-gent"
   [dealer]
   (setup-loop dealer)
   (register)
   (info (<< "Re-gent ~{version} is running!"))
   (println (<< "Re-gent ~{version} is running!")))

(defn launch [host port]
  (setup host port)
  (start))

(defn fail []
  (println "Host and port are required")
  (System/exit 1))

(defn -main [& args]
  (let [host (first args) port (second args)]
    (if (and host port)
      (launch host port)
      (fail))))
