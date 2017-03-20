(ns karma-tracker.resources
  (:require [clj-time.core :refer [seconds]]
            [environ.core :refer [env]]
            [karma-tracker
             [cache :as cache]
             [events-repository :as events-repo]
             [github :as gh]]))

(defn languages-cache-max-age []
  (-> (env :languages-cache-max-age 86400)
      (seconds)))

(defn get-resources []
  {:github-conn     (delay (gh/new-connection))
   :events-storage  (delay (events-repo/connect))
   :languages-cache (delay (cache/connect (cache/connection "repos-languages-cache"
                                                            (languages-cache-max-age))))})
