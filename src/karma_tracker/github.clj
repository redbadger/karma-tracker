(ns karma-tracker.github
  (:require [environ.core :refer [env]]
            [tentacles
             [events :as events]
             [orgs :as orgs]
             [repos :as repos]]))

(defprotocol ClientProtocol
  (organisation-members [conn organisation])
  (performed-events [conn user-login]))

(defrecord Connection [opts]
  ClientProtocol

  (organisation-members [this organisation]
    (orgs/members organisation opts))

  (performed-events [this user-login]
    (events/performed-events user-login opts)))

(defn new-connection
  ([user token default-opts]
   (->Connection (merge {:auth      (str user ":" token)
                         :per-page  100
                         :all-pages true}
                        default-opts)))
  ([]
   (new-connection (env :github-user) (env :github-token) {})))

(defn organisation-performed-events [conn organisation-name]
  (->> organisation-name
       (organisation-members conn)
       (map :login)
       (mapcat (partial performed-events conn))))

(comment
  (def events
    (let [conn (new-connection)]
      (organisation-performed-events conn "redbadger")))

  (require '[karma-tracker.transformers.events :as e]
           '[karma-tracker.aggregation :as a])

  (nth events 5)
  (nth (sequence e/transform events) 5)
  (repos/languages "redbadger" "website-honestly")

  (->> events
       (sequence e/transform)
       a/aggregate))
