(ns karma-tracker.report
  (:require [cljstache.core :refer [render-resource]]))

(defn generate-report [report-data]
  (render-resource "report/template.mustache" report-data))

(defn save-report [report]
  (spit "report.html" report))
