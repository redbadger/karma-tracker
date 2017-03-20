(ns karma-tracker.query.users
  (:require [clojure.string :as string]))

(defn users [events]
  "Aggregates events into a set of users login names"
  (->> events
       (map :user)
       set
       (sort-by string/lower-case)))
