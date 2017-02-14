(ns karma-tracker.resources
  (:require [karma-tracker.github :as gh]
            [karma-tracker.events-repository :as events-repo]))

(defn get-resources []
  {:github-conn    (delay (gh/new-connection))
   :events-storage (delay (events-repo/connect))})
