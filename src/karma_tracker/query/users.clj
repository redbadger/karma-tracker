(ns karma-tracker.query.users)

(defn users [events]
  "Aggregates events into a set of users login names"
  (->> events (map :user) set))
