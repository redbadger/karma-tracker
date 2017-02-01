(ns karma-tracker.events-repository
  (:require [monger.core :as mongo]
            [monger.collection :as collection]
            monger.joda-time
            [clj-time.format :as format]
            [clojure.walk :refer [postwalk]]
            [clojure.set :refer [rename-keys]]
            [environ.core :refer [env]])
  (:import [com.mongodb DB]
           [org.joda.time DateTimeZone]))


(comment "Download and save events:"
  (require '[karma-tracker.github :as github]
           '[karma-tracker.events-repository :as repo]
           '[clj-time.core :refer [now weeks ago]])

  (def client (github/new-connection))
  (def db (repo/connect))

  (defn fetch-users [organisation]
    (map :login (github/organisation-members client organisation)))

  (defn fetch-events [organisation]
    (mapcat (partial github/performed-events client) (fetch-users organisation)))

  (repo/add db (fetch-events "redbadger"))
  (repo/fetch db (-> 2 weeks ago) (now)))


(def events-collection "events")
(def date-time-format (format/formatter :date-time-no-ms))
(DateTimeZone/setDefault DateTimeZone/UTC)

(defn try-parse-date-time
  "Attempts to parse the given value as a date-time.
  Returns the value unmodified if it cannot be parsed."
  [value]
  (try (format/parse date-time-format value)
    (catch Exception _ value)))

(defn parse-date-times
  "Traverses the given data structure and parses any date-time strings found within."
  [data]
  (postwalk try-parse-date-time data))

(defprotocol EventsRepository
  (add [_ events] "Saves events.")
  (fetch [_ start finish] "Loads events that occurred within the specified time period [start, finish)."))

(extend-type DB
  EventsRepository

  (add [db events]
    (doseq [event events]
      (collection/update db
                         events-collection
                         {:_id (event :id)}
                         (-> event (dissoc :id) parse-date-times)
                         {:upsert true})))

  (fetch [db start finish]
    (map #(rename-keys % {:_id :id})
         (collection/find-maps db events-collection {:created_at {"$gte" start, "$lt" finish}}))))

(defn connect
  "Connects to MongoDB and returns the database client."
  ([] (connect (env :mongodb-uri)))
  ([uri] (:db (mongo/connect-via-uri uri))))
