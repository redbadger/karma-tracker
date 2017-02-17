(ns karma-tracker.report
  (:require [cljstache.core :refer [render-resource]]
            [karma-tracker.events-repository :refer [get-events-for-month]]
            [karma-tracker.events :refer [transform]]
            [karma-tracker.aggregation :refer [aggregate]]
            [karma-tracker.augmentation :refer [augment]]))

(defn render [report-data]
  (render-resource "report/template.mustache" report-data))

(defn save [report]
  (spit "report.html" report))

(defn get-data [github-conn events-storage year month]
  (->> (get-events-for-month events-storage year month)
       (sequence transform)
       aggregate
       (augment github-conn)))

(defn make-report [github-conn events-storage year month]
  (-> (get-data github-conn events-storage year month)
      render
      save))
