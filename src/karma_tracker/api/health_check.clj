(ns karma-tracker.api.health-check
  (:require [compojure.core :refer :all]
            [monger.core :as mongo]
            [monger.result :as result]))

(defn health-check [resources _]
  (let [success (-> @(:events-storage resources)
                    (mongo/command {:ping 1})
                    result/acknowledged?)]
    (if success
      {:status 200}
      {:status 503})))

(defn health-check-routes [resources]
  (GET "/health-check" [] (partial health-check resources)))
