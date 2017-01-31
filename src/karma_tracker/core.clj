(ns karma-tracker.core
  (:require [karma-tracker.github :as github]
            [karma-tracker.events-repository :as repo])
  (:gen-class))

(def client (github/new-connection))
(def db (repo/connect))

(defn fetch-users [organisation]
  (map :login (github/organisation-members client organisation)))

(defn fetch-events [organisation]
  (mapcat (partial github/performed-events client) (fetch-users organisation)))

(defn -main [& args]
  (repo/add db (fetch-events "redbadger")))
