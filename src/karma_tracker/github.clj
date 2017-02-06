(ns karma-tracker.github
  (:require [environ.core :refer [env]]
            [tentacles
             [events :as events]
             [orgs :as orgs]
             [repos :as repos]]))

(defn error-response? [{:keys [headers status body] :as response}]
  (and (map? headers)
       (int? status)
       (map? body)))

(defprotocol ClientProtocol
  (organisation-members [conn organisation])
  (performed-events [conn user-login])
  (repo-languages [conn user-login repo]))

(defrecord Connection [opts]
  ClientProtocol

  (organisation-members [this organisation]
    (orgs/members organisation opts))

  (performed-events [this user-login]
    (events/performed-events user-login opts))

  (repo-languages [this user-login repo]
    (repos/languages user-login repo opts)))

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
