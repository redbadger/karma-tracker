(ns karma-tracker.report
  (:require [clj-time.core :refer [now]]
            [cljstache.core :refer [render-resource]]
            [karma-tracker
             [aggregation :refer [aggregate]]
             [augmentation :refer [augment]]
             [events :refer [transform]]
             [events-repository :refer [get-events-for-month]]]))

(defn render [report-data]
  (render-resource "report/template.mustache" report-data))

(defn save [report year month]
  (spit (str "report-" year "-" month ".html") report)
  report)

(defn add-meta-data [report year month]
  (assoc report :meta {:created    (now)
                       :data-year  year
                       :data-month month}))

(defn get-data [github-conn events-storage year month]
  (->> (get-events-for-month events-storage year month)
       (sequence transform)
       aggregate
       (augment github-conn)))

(defn make-report [github-conn events-storage year month]
  (-> (get-data github-conn events-storage year month)
      (add-meta-data year month)
      render
      (save year month)))
