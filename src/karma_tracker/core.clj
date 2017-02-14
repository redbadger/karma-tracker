(ns karma-tracker.core
  (:gen-class)
  (:require [karma-tracker
             [cli :as cli]
             [commands :as ex]
             [resources :refer [get-resources]]]))

(defn get-execution-fn []
  (partial ex/execute (get-resources)))

(defn -main [& args]
  (cli/run (get-execution-fn) args))
