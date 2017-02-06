(ns karma-tracker.augmentation
  (:require [karma-tracker.github :as gh]
            [clojure.string :as s]
            [tentacles.core :as tc]))

(defn- convert-to-percentage [total value]
  (* (double (/ value total)) 100))

(defn- map-vals->percentage [_map]
  (let [total (->> _map vals (reduce +))
        convert (partial convert-to-percentage total)]
    (->> _map
         (mapcat (fn [[key value]]
                   [key [value (convert value)]]))
         (apply hash-map))))

(defn- map-vals->rank [_map]
  (sort (fn [[_ value1] [_ value2]]
          (compare value2 value1))
        _map))

(defn- load-languages [github-conn repos]
  (->> repos
       (map (fn [repo]
              [repo (s/split repo #"\/")]))
       (map (fn [[full-name [user repo]]]
              [full-name (gh/repo-languages github-conn user repo)]))
       (remove (fn [[full-name response]]
                 (gh/error-response? response)))
       (mapcat identity)
       (apply hash-map)))

(defn repos-languages [github-conn aggregation]
  (assoc aggregation
         :repos-languages
         (load-languages github-conn (:repos aggregation))))

(defn languages-chart [_ aggregation]
  (->> aggregation
       :repos-languages
       vals
       (apply merge-with +)
       map-vals->percentage
       map-vals->rank
       (assoc aggregation :languages-chart)))

(defn repos-contributions-chart [_ aggregation]
  (->> aggregation
       :repos-activity-stats
       (mapcat (fn [[repo-name stats]]
                 [repo-name (->> stats vals (reduce +))]))
       (apply hash-map)
       map-vals->percentage
       map-vals->rank
       (assoc aggregation :repos-contributions-chart)))

(defn overall-activity-chart [_ aggregation]
  (->> aggregation
       :overall-activity-stats
       map-vals->percentage
       map-vals->rank
       (assoc aggregation :overall-activity-chart)))

(def default-augmenters [repos-languages
                         languages-chart
                         repos-contributions-chart
                         overall-activity-chart])

(defn make-augment-fn
  ([augmenters github-conn]
   (->> augmenters
        (map #(partial % github-conn))
        (reverse)
        (apply comp)))
  ([github-conn]
   (make-augment-fn default-augmenters github-conn)))

(defn augment [github-conn aggregation]
  (let [augment-fn (make-augment-fn default-augmenters github-conn)]
    (augment-fn aggregation)))

(comment
  (require '[karma-tracker.transformers.events :as e]
           '[karma-tracker.aggregation :as a]
           '[karma-tracker.github :as gh])

  (def conn (gh/new-connection))
  (def events
    (take 200 (gh/organisation-performed-events conn "redbadger")))

  (def repo (->> events
                 (sequence e/transform)
                 a/aggregate
                 (augment conn)))

  (->> repo))
