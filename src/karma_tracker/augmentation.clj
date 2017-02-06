(ns karma-tracker.augmentation
  (:require [karma-tracker.github :as gh]
            [clojure.string :as s]
            [tentacles.core :as tc]))

(defn load-languages [github-conn repos]
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

(defn- languages-total-score [languages-map]
  (->> languages-map
       vals
       (reduce +)))

(defn- convert-to-percentage [total value]
  (* (double (/ value total)) 100))

(defn- map-vals->percentage [_map]
  (let [total (->> _map vals (reduce +))
        convert (partial convert-to-percentage total)]
    (->> _map
         (mapcat (fn [[key value]]
                   [key (convert value)]))
         (apply hash-map))))

(defn- map-vals->rank [_map]
  (sort (fn [[_ value1] [_ value2]]
          (compare value2 value1))
        _map))

(defn languages-chart [aggregation]
  (->> aggregation
       :repos-languages
       vals
       (apply merge-with +)
       map-vals->percentage
       map-vals->rank
       (assoc aggregation :languages-chart)))

(comment
  (require '[karma-tracker.transformers.events :as e]
           '[karma-tracker.aggregation :as a]
           '[karma-tracker.github :as gh])

  (def conn (gh/new-connection))
  (def events
    (gh/organisation-performed-events conn "redbadger"))

  (nth events 3)

  (let [transformed  (sequence e/transform events)]
    (reduce + (remove nil? (map :commits transformed))))

  (def repo (->> events
                 (sequence e/transform)
                 a/aggregate
                 (repos-languages conn)
                 languages-chart))

  (-> repo
      languages-chart
      :languages-chart))
