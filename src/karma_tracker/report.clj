(ns karma-tracker.report
  (:require [karma-tracker.github :as gh]
            [karma-tracker.transformers.events :as events]))

(defn repos [events]
  (apply hash-map
         (mapcat (fn [event]
                   [(get-in event [:repo :name])
                    (:repo event)])
                 events)))

(defn users [events]
  (apply hash-map
         (mapcat (fn [event]
                   [(get-in event [:user :login])
                    (:user event)])
                 events)))

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

(defn report-builder [& {:as reducers-map}]
  (fn [base-report events]
    (merge base-report
           (apply hash-map
                  (mapcat (fn [[name reducer]]
                            [name (reducer events)])
                          reducers-map)))))

(def build-report
  (report-builder :users users
                  :repos repos
                  :users-repos users-repos
                  :repos-users repos-users))

(defn new-report [organisation-name from-date to-date]
  {:info {:organisation organisation-name
          :from from-date
          :to to-date}})

(comment
  (def conn (gh/new-connection))
  (def performed-events
    (->> "user-login"
         (gh/performed-events conn)
         (sequence events/transform)))
  (build-report (new-report "redbadger" "2016-01-01" "2016-01-31") performed-events))
