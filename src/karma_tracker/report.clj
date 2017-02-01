(ns karma-tracker.report
  (:require [karma-tracker.github :as gh]
            [karma-tracker.transformers.events :as events]))

(defn repos [events]
  (->> events
       (map #(get-in % [:repo :name]))
       set))

(defn users [events]
  (->> events
       (map #(get-in % [:user :login]))
       set))

(defn users-repos [events]
  (reduce (fn [report event]
            (update report
                    (get-in event [:user :login])
                    (comp set conj)
                    (get-in event [:repo :name])))
          {}
          events))

(defn repos-users [events]
  (reduce (fn [report event]
            (update report
                    (get-in event [:repo :name])
                    (comp set conj)
                    (get-in event [:user :login])))
          {}
          events))

(defn- activity-stats-inc [value]
  (if (nil? value) 1 (inc value)))

(defn- activity-stats [events]
  (reduce (fn [stats event]
            (update stats
                    (:type event)
                    activity-stats-inc))
          {}
          events))

(defn overall-activity-stats [events]
  (activity-stats events))

(defn repos-activity-stats [events]
  (->> events
       (group-by #(get-in % [:repo :name]))
       (mapcat (fn [[type events]]
                 [type (activity-stats events)]))
       (apply hash-map)))

(defn report-builder [& {:as reducers-map}]
  (fn [base-report events]
    (merge base-report
           (apply hash-map
                  (mapcat (fn [[name reducer]]
                            [name (reducer events)])
                          reducers-map)))))

(def build-report
  (report-builder :overall-activity-stats overall-activity-stats
                  :repos-activity-stats repos-activity-stats
                  :users users
                  :repos repos
                  :users-repos users-repos
                  :repos-users repos-users))

(defn new-report [organisation-name from-date to-date]
  {:info {:organisation organisation-name
          :from from-date
          :to to-date}})
