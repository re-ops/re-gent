(ns re-gent.core
  (:gen-class)
  (:require
   [re-share.zero.common :refer (context)]
   [taoensso.timbre :refer (refer-timbre)]
   [clojure.core.strint :refer (<<)]
   [re-gent.zero.client :refer (setup-client stop-client!)]
   [re-gent.zero.loop :refer (setup-loop stop-loop!)]
   [re-gent.zero.events :refer (setup-events stop-events!)]
   [re-gent.zero.management :refer (register unregister)]
   [re-gent.log :refer (setup-logging)]))

(refer-timbre)

(def version "0.2.1")

(def ctx (atom nil))

(defn stop
  "Stop the loop and unregister"
  ([_] (stop))
  ([]
   (warn "shutting down!")
   (unregister)
   (info "unregister-ed")
   (stop-events!)
   (stop-loop!)
   (Thread/sleep 10)
   (stop-client!)
   (when @ctx
     (.term @ctx)
     (reset! ctx nil))))

(defn add-shutdown []
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop)))

(defn setup [level]
  (setup-logging :level (keyword (or level "info")))
  (add-shutdown))

(defn start
  "start this re-gent"
  ([_] (start "127.0.0.1" "9000"))
  ([host port]
   (reset! ctx (context))
   (let [dealer (setup-client @ctx host port ".curve")]
     (setup-events @ctx)
     (setup-loop dealer))
   (info (<< "Re-gent ~{version} is running!"))
   (println (<< "Re-gent ~{version} is running!"))))

(defn launch [host port level]
  (setup level)
  (start host port))

(defn fail []
  (println "Host and port are required")
  (System/exit 1))

(defn -main [host port & args]
  (if (and host port)
   (launch host port (first args))
   (fail)))
