(ns re-gent.zero.schedule
  "Scheduled function support"
  (:require
   [re-gent.zero.functions]
   [re-cog.zero.scheduled :refer (scheduled-results)]
   [clj-time.core :as t]
   [re-share.schedule :refer (watch halt! seconds)]
   [re-share.core :refer (measure error-m)]
   [clojure.core.match :refer [match]]
   [re-gent.zero.reply :refer (send-)]
   [taoensso.timbre :refer (refer-timbre)]))

(refer-timbre)

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
    (info "scheduling" k)
    (watch k (rest (seconds n))
           (fn []
             (binding [*ns* (find-ns 're-gent.zero.functions)]
               (info "running scheduled" k)
               (swap! scheduled-results update k
                      (fn [q]
                        (try
                          (append q capacity (stamp (apply (eval f) args)))
                          (catch Throwable e
                            (error-m e)
                            (throw e))))))))
    (send- {:reply :scheduled :result {:err "" :out "" :exit 0} :uuid uuid :time 0 :code 0})
    (catch Throwable e
      (send- {:reply :scheduled :result :failed :uuid uuid :error {:out (.getMessage e) :exception (.getName (class e))}})
      (error-m e))))

