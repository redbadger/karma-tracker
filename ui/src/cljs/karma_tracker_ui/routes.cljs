(ns karma-tracker-ui.routes
  (:require-macros [secretary.core :refer [defroute]])
  (:require [re-frame.core :as re-frame]
            [secretary.core :as secretary]
            [cljs-time.format :as format-time]
            [pushy.core :as pushy]))

(def year-month
  (format-time/formatter "YYYY-MM"))

(def parse-year-month
  (partial format-time/parse year-month))

(def format-year-month
  (partial format-time/unparse year-month))

(def history (atom nil))

(def dispatch-route secretary/dispatch!)

(defn match-route [route]
  (when (secretary/locate-route route) route))

(defn start! []
  (when (nil? @history)
    (reset! history (pushy/pushy dispatch-route match-route)))
  (pushy/start! @history))

(defn app-routes []
  (defroute "/" []
    (re-frame/dispatch [:select-initial-view]))

  (defroute #"/overview/(\d{4}-(?:0[1-9]|1[0-2]))" [date]
    (re-frame/dispatch [:select-overview (parse-year-month date)]))

  (start!))

(defn set! [route]
  (pushy/set-token! @history route))

(defn overview [date]
  (str "/overview/" (format-year-month date)))
