(ns karma-tracker.github
  (:require [environ.core :refer [env]]
            [tentacles
             [events :as events]
             [orgs :as orgs]]))

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
