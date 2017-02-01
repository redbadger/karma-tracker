(ns karma-tracker.events-repository
  (:require [monger.core :as mongo]
            [monger.collection :as collection]
            [clojure.set :refer [rename-keys]]
            [environ.core :refer [env]])
  (:import [com.mongodb DB]))


(comment "Download and save events:"
  (require '[karma-tracker.github :as github]
           '[karma-tracker.events-repository :as repo])

  (def client (github/new-connection))
  (def db (repo/connect))

  (defn fetch-users [organisation]
    (map :login (github/organisation-members client organisation)))

  (defn fetch-events [organisation]
    (mapcat (partial github/performed-events client) (fetch-users organisation)))

  (repo/add db (fetch-events "redbadger")))


(def events-collection "events")

(defprotocol EventsRepository
  (add [_ events] "Inserts events into the repository")
  (fetch [_] "Retrieves all saved events from the repository"))

(extend-type DB
  EventsRepository

  (add [db events]
    (doseq [event events]
      (collection/update db
                         events-collection
                         {:_id (event :id)}
                         (dissoc event :id)
                         {:upsert true})))

  (fetch [db]
    (map #(rename-keys % {:_id :id})
         (collection/find-maps db events-collection))))

(defn connect []
  (:db (mongo/connect-via-uri (env :mongodb-uri))))
