(ns karma-tracker.update
  (:require [karma-tracker.github :as gh]
            [karma-tracker.events-repository :as events]))

(defn update-events [github-conn storage organisation]
  (->> organisation
       (gh/organisation-performed-events github-conn)
       (events/add storage)))
