(ns re-gent.zero.management
  "Client registration/processing"
  (:require
   [re-gent.zero.functions]
   [re-share.core :refer (measure)]
   [clojure.core.match :refer  [match]]
   [taoensso.timbre :refer (refer-timbre)]
   [re-gent.zero.reply :refer (send-)]))

(refer-timbre)

(defn run-fn
  "Run a function from the server"
  [f args name uuid]
  (debug "executing" name uuid)
  (binding [*ns* (find-ns 're-gent.zero.functions)]
    (try
      (let [m (measure (fn [] (apply (eval f) args)))]
        (send- (merge {:reply :execute :name name :uuid uuid} m)))
      (catch Throwable e
        (send- {:reply :execute :result :failed :name name :uuid uuid :error {:out (.getMessage e) :exception (.getName (class e))}})
        (error "failed to call f" e)))))

(defn process
  "process server requests"
  [request]
  (match [request]
    [{:request :execute :fn f :args args :name name :uuid uuid}] (run-fn f args name uuid)
    [{:response :ok :on {:request :register}}] (info "registered successfuly")
    :else (info "no handler found for" request)))

(defn register []
  (send- {:request :register})
  (info "registering"))

(defn unregister []
  (send- {:request :unregister}))
