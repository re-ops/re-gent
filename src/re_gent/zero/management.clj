(ns re-gent.zero.management
  "Client registration/processing"
  (:require
   [clj-time.core :as t]
   [re-cog.zero.scheduled :refer (scheduled-results)]
   [re-gent.zero.functions]
   [re-share.schedule :refer (watch halt! seconds)]
   [re-share.core :refer (measure error-m)]
   [clojure.core.match :refer  [match]]
   [taoensso.timbre :refer (refer-timbre)]
   [re-gent.zero.reply :refer (send-)]))

(refer-timbre)

(defn run-fn
  "Run a function from the server"
  [f args uuid]
  (debug "executing" uuid)
  (binding [*ns* (find-ns 're-gent.zero.functions)]
    (try
      (let [m (measure (fn [] (apply (eval f) args)))]
        (send- (merge {:reply :execute :uuid uuid} m)))
      (catch clojure.lang.Compiler$CompilerException e
        (send- {:reply :execute :result :failed :uuid uuid :error {:out (.getMessage (.getCause e)) :exception (.getName (class e))}})
        (error-m e))
      (catch Throwable e
        (send- {:reply :execute :result :failed :uuid uuid :error {:out (.getMessage e) :exception (.getName (class e))}})
        (error-m e)))))

(defn queue []
  (clojure.lang.PersistentQueue/EMPTY))

(defn append [curr cap r]
  (let [q (if curr curr (queue))
        q' (conj q r)]
    (if (> (count q') cap)
      (pop q') q')))

(defn stamp [m]
  (assoc m :timestamp (.getMillis (t/now))))

(defn schedule-fn
  "Schedule an fn every n seconds"
  [f k n capacity args uuid]
  (try
    (debug "scheduling" k)
    (watch k (rest (seconds n))
           (fn []
             (binding [*ns* (find-ns 're-gent.zero.functions)]
               (info "running scheduled" k)
               (swap! scheduled-results update k
                      (fn [q]
                        (append q capacity (stamp (apply (eval f) args))))))))
    (send- {:reply :scheduled :result {:err "" :out "" :exit 0} :uuid uuid :time 0 :code 0})
    (catch Throwable e
      (send- {:reply :scheduled :result :failed :uuid uuid :error {:out (.getMessage e) :exception (.getName (class e))}})
      (error-m e))))

(defn process
  "process server requests"
  [request]
  (match [request]
    [{:request :execute :fn f :args args :uuid uuid}] (run-fn f args uuid)
    [{:request :schedule :fn f :args args :k k :n n :capacity c :uuid uuid}] (schedule-fn f k n c args uuid)
    [{:response :ok :on {:request :register}}] (info "registered successfuly")
    :else (info "no handler found for" request)))

(defn register []
  (send- {:request :register})
  (info "registering"))

(defn unregister []
  (send- {:request :unregister}))
