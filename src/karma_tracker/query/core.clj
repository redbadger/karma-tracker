(ns karma-tracker.query.core
  (:require [clj-time.core :as time]
            [karma-tracker
             [events :refer [transform]]
             [events-repository :as repo]
             [github :as gh]
             [resources :refer [get-resources]]]
            [karma-tracker.query
             [contributions :as c]
             [languages :as l]
             [users :as u]]))

(defn new-query [start end source]
  {:interval (time/interval start end)
   :source source})

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

(defmethod execute-query :default [_ {:keys [source] :as query} _]
  (throw (ex-info (str "Data source " (name source) " is not valid") {:query query})))

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
