(ns karma-tracker-ui.routes
  (:require-macros [secretary.core :refer [defroute]])
  (:require [re-frame.core :as re-frame]
            [secretary.core :as secretary]
            [pushy.core :as pushy]))

(defn hook-browser-navigation! []
  (-> (pushy/pushy secretary/dispatch! #(when (secretary/locate-route %) %))
      pushy/start!))

(defn app-routes []
  (hook-browser-navigation!))
