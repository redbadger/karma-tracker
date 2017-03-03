(ns karma-tracker.query.core
  (:require [clojure.spec :as s]
            [karma-tracker.aggregation :as ag]
            [karma-tracker.events :refer [transform]]
            [karma-tracker.events-repository :as repo]
            [karma-tracker.query.contributions :as c]
            [clj-time.core :as time]))

(s/def ::source keyword?)
(s/def ::interval #(instance? org.joda.time.Interval %))
(s/def ::query (s/keys :req-un [::source ::interval]))

(defrecord Query [interval source])

(defn new-query [interval source]
  (->Query interval source))

(defn execute [resources query]
  (let [events-groups [[1 2 3] [4 5 6]]]
    ))

(defmulti reduce
  (fn [_ query events & args]
    (:collection query)))

(defmethod reduce :repos-contributions [_ _ events])


(comment
  (def events (repo/get-events-for-month (repo/connect) 2017 03))
  (->> events
       (sequence transform)
       (c/repos-contributions))
  )
