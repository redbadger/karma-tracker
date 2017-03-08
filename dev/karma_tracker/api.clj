(ns karma-tracker.api
  (:require [karma-tracker.api.core :refer [api get-execution-fns]]
            [karma-tracker.resources :refer [get-resources]]))

(def dev-handler
  (-> (get-resources)
      (get-execution-fns)
      api))
