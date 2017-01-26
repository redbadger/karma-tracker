(ns scraper.core
  (:require [cheshire.core :as json]
            [clj-time.coerce :as coerce-time]
            [clojure.java.io :as io]
            [clojure.pprint :refer [pprint]]
            [clojure.string :as str]
            [environ.core :refer [env]]
            [tentacles.orgs :as orgs])
  (:import (java.util.zip GZIPInputStream)))

(defn belongs-to? [users event]
  (users (get-in event [:actor :login])))

(defn kebab-case-keyword [snake-case-string]
  (keyword (str/replace snake-case-string "_" "-")))

(defn activity-reader [time-period]
  (->
    (str "http://data.githubarchive.org/" time-period ".json.gz")
    io/input-stream
    GZIPInputStream.
    io/reader))

(defn users-activity [users time-period]
  (with-open [in (activity-reader time-period)]
    (->>
      (json/parsed-seq in kebab-case-keyword)
      (filter (partial belongs-to? users))
      doall)))

(defn organisation-members [organisation]
  (into #{} (map :login (orgs/members organisation {:all-pages true
                                          :per-page 100
                                          :auth (str (env :github-username) ":" (env :github-token))}))))

(defn organisation-activity [organisation time-period]
  (users-activity (organisation-members organisation) time-period))

(defn -main [& args]
  (let [[organisation time-period] args]
    (pprint (organisation-activity organisation time-period))))
