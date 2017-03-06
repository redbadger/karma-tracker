(ns karma-tracker.resources
  (:require [karma-tracker
             [events-repository :as events-repo]
             [github :as gh]
             [cache :as cache]]
            [clj-time.core :refer [seconds]]
            [environ.core :refer [env]]))

(defn languages-cache-max-age []
  (-> (env :languages-cache-max-age 86400)
      (seconds)))

(defn get-resources []
  {:github-conn     (delay (gh/new-connection))
   :events-storage  (delay (events-repo/connect))
   :languages-cache (delay (cache/connect (cache/connection "repos-languages-cache"
                                                            (languages-cache-max-age))))})
