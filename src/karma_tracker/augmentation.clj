(ns karma-tracker.augmentation
  (:require [karma-tracker.github :as gh]
            [clojure.string :as s]
            [tentacles.core :as tc]))

(defn- convert-to-percentage [total value]
  (->> (/ value total)
       double
       (* 100)
       (format "%.1f%%")))

(defn- map-vals->percentage [_map]
  "Convert the values of the map to percentage"
  (let [total (->> _map vals (reduce +))
        convert (partial convert-to-percentage total)]
    (->> _map
         (mapcat (fn [[key value]]
                   [key [value (convert value)]]))
         (apply hash-map))))

(defn- map-vals->rank [_map]
  "Convert the map in a list sorted by the map values"
  (sort (fn [[_ value1] [_ value2]]
          (compare value2 value1))
        _map))

(defn- rank->maps [rank]
  "Convert every value from the vector to a map to allow the
   template engine to read the values."
  (map (fn [[item [value percentage]]]
         {:item       item
          :value      value
          :percentage percentage})
       rank))

(defn- load-languages [github-conn repos]
  (->> repos
       (map (fn [repo]
              [repo (s/split repo #"\/")]))
       (map (fn [[full-name [user repo]]]
              [full-name (gh/repo-languages github-conn user repo)
               ]))
       (remove (fn [[full-name response]]
                 (gh/error-response? response)))
       (mapcat identity)
       (apply hash-map)))

(defn repos-languages [github-conn aggregation]
  (let [repos (->> aggregation
                   :repos-contributions-chart
                   (map :item))]
    (assoc aggregation
           :repos-languages
           (load-languages github-conn repos))))

(defn languages-chart [_ aggregation]
  (->> aggregation
       :repos-languages
       vals
       (apply merge-with +)
       map-vals->percentage
       map-vals->rank
       (take 10)
       (map (fn [[language values]]
              [(name language) values]))
       rank->maps
       (assoc aggregation :languages-chart)))

(defn repos-contributions-chart [_ aggregation]
  (->> aggregation
       :repos-activity-stats
       (mapcat (fn [[repo-name stats]]
                 [repo-name (->> stats vals (reduce +))]))
       (apply hash-map)
       map-vals->percentage
       map-vals->rank
       (take 20)
       rank->maps
       (assoc aggregation :repos-contributions-chart)))

(def contributions-type-labels {:commits                     "Commits"
                                :push                        "Pushes"
                                :issue-comment               "Issues' comments"
                                :pull-request                "Pull requests"
                                :issue                       "Issues"
                                :pull-request-review-comment "Reviews' comments"})

(defn overall-activity-chart [_ aggregation]
  (->> aggregation
       :overall-activity-stats
       map-vals->percentage
       map-vals->rank
       (map (fn [[type values]]
              [(get contributions-type-labels type) values]))
       rank->maps
       (assoc aggregation :overall-activity-chart)))

(def default-augmenters [repos-contributions-chart
                         overall-activity-chart
                         repos-languages
                         languages-chart])

(defn make-augment-fn
  "It return the augmentation function composing the list of augmenters
   functions passed as argument. Each of them has to require the GitHub
   connection as first argument and the aggregated data as second."
  ([augmenters github-conn]
   (->> augmenters
        (map #(partial % github-conn))
        (reverse)
        (apply comp)))
  ([github-conn]
   (make-augment-fn default-augmenters github-conn)))

(defn augment [github-conn aggregation]
  "Execute the augmentation with the default augmenters functions"
  (let [augment-fn (make-augment-fn default-augmenters github-conn)]
    (augment-fn aggregation)))

(comment
  (require '[karma-tracker.events :as e]
           '[karma-tracker.aggregation :as a]
           '[karma-tracker.github :as gh]
           '[karma-tracker.report :as r]
           '[criterium.core :as c]
           '[cljstache.core :refer [render-resource]])

  (-> repo
      r/generate-report
      r/save-report)

  (def conn (gh/new-connection))
  (def events
    (gh/organisation-performed-events conn "redbadger"))

  (def repo (->> events
                 (sequence e/transform)
                 a/aggregate
                 (augment conn)))

  (->> repo :languages-chart))
