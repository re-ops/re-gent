(ns re-gent.core
  (:gen-class)
  (:require
    [re-share.zero.common :refer (context)]
    [taoensso.timbre :refer (refer-timbre)]
    [clojure.core.strint :refer (<<)]
    [re-gent.zero.client :refer (setup-client)]
    [re-gent.zero.loop :refer (setup-loop stop-loop!)]
    [re-gent.zero.management :refer (register unregister)]
    [re-gent.log :refer (setup-logging)]))

(refer-timbre)

(def version "0.2.0")

(def ctx (atom nil))
(def selector (atom nil))

(defn stop
  "Stop the loop and unregister"
  ([_] (stop))
  ([]
   (warn "shutting down!")
   (unregister)
   (info "unregister-ed")
   (when @selector
     (.close @ctx @selector))
   (info "selector closed")
   (stop-loop!)
   (info "loop stopped") 
   (Thread/sleep 1000)
   (when @ctx
     (.close @ctx))
   (reset! ctx nil)
   (reset! selector nil)
   (info "ctx terminated") 
   ))


(defn add-shutdown []
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop)))

(defn setup []
  (setup-logging)
  (add-shutdown))

(defn start
  "start this re-gent"
  ([_] (start "127.0.0.1" "9000"))
  ([host port]
    (reset! ctx (context))
    (reset! selector (.selector @ctx))
    (let [dealer (setup-client @ctx host port ".curve")]
      (setup-loop dealer @selector))
    (register)
    (info (<< "Re-gent ~{version} is running!"))
    (println (<< "Re-gent ~{version} is running!"))))

(defn launch [host port]
  (setup)
  (start host port))

(defn fail []
  (println "Host and port are required")
  (System/exit 1))

(defn -main [& args]
  (let [host (first args) port (second args)]
    (if (and host port)
      (launch host port)
      (fail))))
