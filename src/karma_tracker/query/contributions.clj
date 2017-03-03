(ns karma-tracker.query.contributions
  (:require [karma-tracker.query.utils :refer :all]))

(defn- count-commits [events]
  "Calculate the sum of the commits in the events.
   The PushEvent is the only one containing the :commit field"
  (->> events
       (map :commits)
       (remove nil?)
       (reduce +)))

(defn- commits-stats [events]
  (let [commits (count-commits events)]
    (when (> commits 0)
      {:commit commits})))

(defn- activity-stats-inc [value]
  (if (nil? value) 1 (inc value)))

(defn- normalise-stats [{issues :issue-comment prs :pull-request-review-comment
                         :or {issues 0 prs 0}
                         :as stats}]
  (-> stats
      (dissoc :push)
      (assoc :comment (+ issues prs))
      (dissoc :issue-comment :pull-request-review-comment)))

(defn- activity-stats [events]
  "Aggregates events into a map of events type and the amount of each type"
  (-> (fn [stats event]
        (update stats
                (:type event)
                activity-stats-inc))
      (reduce {} events)
      (merge (commits-stats events))
      (normalise-stats)))

;; (def contributions-type-labels {:commits                     "Commits"
;;                                 :push                        "Pushes"
;;                                 :issue-comment               "Issues' comments"
;;                                 :pull-request                "Pull requests"
;;                                 :issue                       "Issues"
;;                                 :pull-request-review-comment "Reviews' comments"})

(defn contributions [events]
  (->> events
       activity-stats
       map-vals->percentage
       map-vals->rank
       ;; (map (fn [[type values]]
       ;;        [(get contributions-type-labels type) values]))
       rank->maps))

(defn- repos-add-percentage [repos-contributions]
  "Convert the values of the map to percentage"
  (let [total (->> repos-contributions (map :total) (reduce +))
        convert (partial convert-to-percentage total)]
    (->> repos-contributions
         (map (fn [value]
                (assoc value :percentage (convert (:total value))))))))

(defn repos-contributions [events]
  "Activity stats grouped by repository"
  (->> events
       (group-by :repo)
       (map (fn [[repo-name events]]
              [repo-name (activity-stats events)]))
       (map (fn [[repo-name stats]]
              (let [total (->> stats vals (reduce +))]
                (assoc stats
                       :repo repo-name
                       :total total))))
       (repos-add-percentage)
       (sort (fn [{total1 :total} {total2 :total}]
               (compare total2 total1)))))
