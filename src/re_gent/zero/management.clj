(ns re-gent.zero.management
  "Client registration/processing"
  (:require
   [re-gent.zero.functions]
   [re-gent.zero.schedule :refer (schedule-fn)]
   [re-cog.zero.scheduled :refer (scheduled-results)]
   [re-share.core :refer (measure error-m)]
   [clojure.core.match :refer [match]]
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
