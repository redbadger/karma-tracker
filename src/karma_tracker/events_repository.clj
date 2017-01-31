(ns karma-tracker.events-repository
  (:require [monger.core :as mongo]
            [monger.collection :as collection]
            [clojure.set :refer [rename-keys]]
            [environ.core :refer [env]])
  (:import [com.mongodb DB]))

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
