(ns re-gent.core
  (:gen-class)
  (:require
   [re-share.zero.common :refer (context)]
   [taoensso.timbre :refer (refer-timbre)]
   [clojure.core.strint :refer (<<)]
   [re-share.zero.events :as evn]
   [re-gent.zero.client :as client]
   [re-gent.zero.loop :as lop]
   [re-gent.zero.events :refer (handle)]
   [re-gent.facts :as facts]
   [re-gent.zero.management :refer (register unregister)]
   [re-gent.log :refer (setup-logging)])
  (:import [java.util.concurrent ThreadPoolExecutor ThreadPoolExecutor$AbortPolicy TimeUnit ArrayBlockingQueue]))

(refer-timbre)

(def version "0.6.5")

(def ctx (atom nil))

(defn stop
  "Stop the loop and unregister"
  ([_] (stop))
  ([]
   (warn "shutting down!")
   (unregister)
   (info "unregister-ed")
   (evn/stop)
   (lop/stop)
   (Thread/sleep 10)
   (client/stop)
   (when @ctx
     (.term @ctx)
     (reset! ctx nil))
   (facts/stop)))

(defn add-shutdown []
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop)))

(def pool (atom nil))

(defn thread-pool []
  (let [queue (ArrayBlockingQueue. 10)]
    (ThreadPoolExecutor. 10 20 180 TimeUnit/SECONDS queue (ThreadPoolExecutor$AbortPolicy.))))

(defn setup
  ([]
   (setup :info))
  ([level]
   (reset! pool (thread-pool))
   (set-agent-send-off-executor! @pool)
   (facts/setup)
   (setup-logging :level (keyword (or level "info")))
   (add-shutdown)))

(defn start
  "start this re-gent"
  ([_] (start "127.0.0.1" "9000"))
  ([host port]
   (reset! ctx (context))
   (let [dealer (client/start @ctx host port ".curve")]
     (evn/start @ctx handle)
     (lop/start dealer))
   (facts/start)
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
