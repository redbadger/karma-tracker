(ns karma-tracker.api.core
  (:require [compojure.core :refer :all]
            [karma-tracker.api.health-check :refer [health-check-routes]]
            [karma-tracker.api.query :refer [query-routes]]
            [karma-tracker.query.core :as query]
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

(defn get-execution-fns [resources]
  {:query (partial query/execute resources)})

(defn api [resources]
  (-> (routes
       (health-check-routes resources)
       (query-routes (-> resources get-execution-fns :query)))
      wrap-handler))

(defn run-server [resources]
  (println "Api server running on port 8000")
  (run-jetty (api resources)
             {:port 8000}))
