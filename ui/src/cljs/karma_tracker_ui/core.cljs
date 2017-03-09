(ns karma-tracker-ui.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [day8.re-frame.http-fx]
            [karma-tracker-ui.effects]
            [karma-tracker-ui.events]
            [karma-tracker-ui.subs]
            [karma-tracker-ui.routes :as routes]
            [karma-tracker-ui.views :as views]
            [karma-tracker-ui.config :as config]))

(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/root]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (routes/app-routes)
  (re-frame/dispatch-sync [:initialize-db])
  (dev-setup)
  (mount-root))
