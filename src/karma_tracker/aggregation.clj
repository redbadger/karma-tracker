(ns karma-tracker.aggregation
  (:require [karma-tracker.github :as gh]
            [karma-tracker.transformers.events :as events]))

(defn repos [events]
  (->> events (map :repo) set))

(defn users [events]
  (->> events (map :user) set))

(defn users-repos [events]
  (reduce (fn [report event]
            (update report
                    (:user event)
                    (comp set conj)
                    (:repo event)))
          {}
          events))

(defn repos-users [events]
  (reduce (fn [report event]
            (update report
                    (:repo event)
                    (comp set conj)
                    (:user event)))
          {}
          events))

(defn- activity-stats-inc [value]
  (if (nil? value) 1 (inc value)))

(defn- activity-stats [events]
  (merge
   (reduce (fn [stats event]
             (update stats
                     (:type event)
                     activity-stats-inc))
           {}
           events)
   (commits-stats events)))

(defn- count-commits [events]
  (->> events
       (map :commits)
       (remove nil?)
       (reduce +)))

(defn- commits-stats [events]
  (let [commits (count-commits events)]
    (when (> commits 0)
      {:commits commits})))

(defn overall-activity-stats [events]
  (activity-stats events))

(defn repos-activity-stats [events]
  (->> events
       (group-by :repo)
       (mapcat (fn [[type events]]
                 [type (activity-stats events)]))
       (apply hash-map)))

(defn make-aggregator [reducers-map]
  (fn [events]
    (apply hash-map
           (mapcat (fn [[name reducer]]
                     [name (reducer events)])
                   reducers-map))))

(def default-aggregators {:overall-activity-stats overall-activity-stats
                          :repos-activity-stats   repos-activity-stats
                          :users                  users
                          :repos                  repos
                          :users-repos            users-repos
                          :repos-users            repos-users})

(def aggregate
  (make-aggregator default-aggregators))
