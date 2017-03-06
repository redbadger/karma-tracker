(ns karma-tracker.query.core
  (:require [clojure.spec :as s]
            [karma-tracker.aggregation :as ag]
            [karma-tracker.events :refer [transform]]
            [karma-tracker.events-repository :as repo]
            [karma-tracker.query.contributions :as c]
            [karma-tracker.query.languages :as l]
            [karma-tracker.query.users :as u]
            [karma-tracker.resources :refer [get-resources]]
            [karma-tracker.github :as gh]
            [clj-time.core :as time]))

(s/def ::source keyword?)
(s/def ::interval #(instance? org.joda.time.Interval %))
(s/def ::query (s/keys :req-un [::source ::interval]))

(defrecord Query [interval source])

(defn new-query [start end source]
  (->Query (time/interval start end) source))

(defn load-events [events-storage interval]
  (->> (repo/fetch events-storage
                   (time/start interval)
                   (time/end interval))
       (sequence transform)))

(defmulti execute-query
  (fn [_ {:keys [source] :as query} & args]
    source))

(defmethod execute-query :users [_ _ events]
  (u/users events))

(defmethod execute-query :contributions [_ _ events]
  (c/contributions events))

(defmethod execute-query :repos-contributions [_ _ events]
  (c/repos-contributions events))

(defmethod execute-query :languages [{:keys [languages-cache github-conn]} _ events]
  (l/languages @languages-cache @github-conn events))

(defmethod execute-query :repos-languages [{:keys [languages-cache github-conn]} _ events]
  (l/repos-languages @languages-cache @github-conn events))

(defn execute [resources {:keys [interval] :as query}]
  (let [events (load-events @(:events-storage resources) interval)]
    (execute-query resources query events)))

(comment
  (def events (repo/get-events-for-month (repo/connect) 2017 2))
  (def gh (gh/new-connection))
  (->> events
       (sequence transform)
       (l/languages gh))
  (execute (get-resources) (new-query (time/date-time 2017 2 1)
                                      (time/date-time 2017 2 28)
                                      :repos-languages))
  )
