(ns karma-tracker.api.core
  (:require [compojure.core :refer :all]
            [karma-tracker.api.query :refer [query-routes]]
            [karma-tracker.api.info :refer [info-routes]]
            [karma-tracker.query.core :as query]
            [karma-tracker.info :as info]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware
             [cors :refer [wrap-cors]]
             [keyword-params :refer [wrap-keyword-params]]
             [params :refer [wrap-params]]]))

(defn wrap-handler [handler]
  (-> handler
      (wrap-keyword-params)
      (wrap-params)
      (wrap-cors :access-control-allow-origin [#".*"]
                 :access-control-allow-methods [:get])))

(defn api [execution-fns]
  (-> (routes
       (query-routes (:query execution-fns))
       (info-routes (:info execution-fns)))
      wrap-handler))

(defn get-execution-fns [resources]
  {:query (partial query/execute resources)
   :info  (partial info/retrieve resources)})

(defn run-server [resources]
  (println "Api server running on port 8000")
  (run-jetty (api (get-execution-fns resources))
             {:port 8000}))
