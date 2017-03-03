(ns karma-tracker.query.utils)

(defn convert-to-percentage [total value]
  (->> (/ value total)
       double
       (* 100)
       (format "%.1f%%")))

(defn map-vals->percentage [_map]
  "Convert the values of the map to percentage"
  (let [total (->> _map vals (reduce +))
        convert (partial convert-to-percentage total)]
    (->> _map
         (mapcat (fn [[key value]]
                   [key [value (convert value)]]))
         (apply hash-map))))

(defn map-vals->rank [_map]
  "Convert the map in a list sorted by the map values"
  (sort (fn [[_ value1] [_ value2]]
          (compare value2 value1))
        _map))

(defn rank->maps [rank]
  "Convert every value from the vector to a map to allow the
   template engine to read the values."
  (map (fn [[item [value percentage]]]
         {:item       item
          :value      value
          :percentage percentage})
       rank))
