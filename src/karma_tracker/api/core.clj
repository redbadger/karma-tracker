(ns karma-tracker.api.core
  (:require [compojure.core :refer :all]
            [karma-tracker.api.query :refer [query-routes]]
            [karma-tracker.query.core :as query]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware
             [cors :refer [wrap-cors]]
             [keyword-params :refer [wrap-keyword-params]]
             [params :refer [wrap-params]]]))

(defn api [execution-fns]
  (-> (routes
       (query-routes (:query execution-fns)))
      (wrap-keyword-params)
      (wrap-params)
      (wrap-cors :access-control-allow-origin [#".*"]
                 :access-control-allow-methods [:get])))

(defn get-execution-fns [resources]
  {:query (partial query/execute resources)})

(defn run-server [resources]
  (println "Api server running on port 8000")
  (run-jetty (api (get-execution-fns resources))
             {:port 8000}))
