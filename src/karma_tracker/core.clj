(ns karma-tracker.core
  (:gen-class)
  (:require [karma-tracker
             [cli :as cli]
             [commands :as ex]
             [resources :refer [get-resources]]])
  (:import [org.joda.time DateTimeZone]))

(defn setup []
  (DateTimeZone/setDefault DateTimeZone/UTC))

(defn get-execution-fn []
  (partial ex/execute (get-resources)))

(defn -main [& args]
  (setup)
  (cli/run (get-execution-fn) args))
