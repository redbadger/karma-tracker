(ns karma-tracker.aggregation
  (:require [karma-tracker
             [events :as events]
             [github :as gh]]))

(defn repos [events]
  "Aggregates events into a set of repositories names"
  (->> events (map :repo) set))

(defn users [events]
  "Aggregates events into a set of users login names"
  (->> events (map :user) set))

(defn users-repos [events]
  "Aggregates events into a map where users are associated
   with the repositories they contributed to"
  (reduce (fn [report event]
            (update report
                    (:user event)
                    (comp set conj)
                    (:repo event)))
          {}
          events))

(defn repos-users [events]
  "Aggregates events into a map where repos are associated
   with the users who contributed"
  (reduce (fn [report event]
            (update report
                    (:repo event)
                    (comp set conj)
                    (:user event)))
          {}
          events))

(defn- activity-stats-inc [value]
  (if (nil? value) 1 (inc value)))

(defn- count-commits [events]
  "Calculate the sum of the commits in the events.
   The PushEvent is the only one containing the :commit field"
  (->> events
       (map :commits)
       (remove nil?)
       (reduce +)))

(defn- commits-stats [events]
  (let [commits (count-commits events)]
    (when (> commits 0)
      {:commits commits})))

(defn- activity-stats [events]
  "Aggregates events into a map of events type and the amount of each type"
  (merge
   (reduce (fn [stats event]
             (update stats
                     (:type event)
                     activity-stats-inc))
           {}
           events)
   (commits-stats events)))

(defn overall-activity-stats [events]
  (activity-stats events))

(defn repos-activity-stats [events]
  "Activity stats grouped by repository"
  (->> events
       (group-by :repo)
       (mapcat (fn [[type events]]
                 [type (activity-stats events)]))
       (apply hash-map)))

(defn make-aggregator [reducers-map]
  "Takes a map of keys and aggregation functions and return the
   aggregation function that takes events as only argument.
   Returns a map in which the keys are taken by the `reducers-map`
   and the values are the execution of each function on the events."
  (fn [events]
    (->> reducers-map
         (mapcat (fn [[name reducer]]
                   [name (reducer events)]))
         (apply hash-map))))

(def default-aggregators {:overall-activity-stats overall-activity-stats
                          :repos-activity-stats   repos-activity-stats
                          :users                  users
                          :repos                  repos
                          :users-repos            users-repos
                          :repos-users            repos-users})

(def aggregate
  (make-aggregator default-aggregators))
