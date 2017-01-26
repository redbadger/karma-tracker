(ns scraper.core
  (:require [tentacles.orgs :as orgs]
            [clj-time.coerce :as coerce-time]
            [clojure.data.json :as json]
            [clojure.string :as str]
            [environ.core :refer [env]]
            [clojure.pprint :refer [pprint]])
  (:import (scraper BigQueryBuilder)
           (com.google.cloud.bigquery QueryRequest QueryParameterValue)))

(def bigquery
  (BigQueryBuilder/build (env :bigquery-project-id)
                         (env :bigquery-client-id)
                         (env :bigquery-client-email)
                         (env :bigquery-private-key)
                         (env :bigquery-private-key-id)))

(defn users-activity-query [users time-period]
  (->
    (QueryRequest/newBuilder (str
      "SELECT actor.login, type, repo.name, payload, created_at"
      " FROM `githubarchive." time-period "`"
      " WHERE actor.login IN UNNEST(@users)"))
    (.addNamedParameter "users" (QueryParameterValue/array (to-array users) String))
    (.setUseLegacySql false)
    .build))

(defn string-value [field]
  (.stringValue field))

(defn json-value [field]
  (json/read-str (.getStringValue field) :key-fn #(keyword (str/replace % "_" "-"))))

(defn time-value [field]
  (coerce-time/from-long (/ (.getTimestampValue field) 1000)))

(def fields
  {:user string-value
   :event string-value
   :repo string-value
   :details json-value
   :time time-value})

(defn parse-field [field transform]
  (transform field))

(defn parse-row [row]
  (zipmap (keys fields) (map parse-field row (vals fields))))

(defn users-activity
  [users time-period]
  (map parse-row (->
    (.query bigquery (users-activity-query users time-period))
    .result
    .iterateAll
    iterator-seq)))

(defn organisation-members [organisation]
  (map :login (orgs/members organisation {:all-pages true
                                          :per-page 100
                                          :auth (str (env :github-username) ":" (env :github-token))})))

(defn organisation-activity [organisation time-period]
  (users-activity (organisation-members organisation) time-period))

(defn -main [& args]
  (let [[organisation time-period] args]
    (pprint (organisation-activity organisation time-period))))
