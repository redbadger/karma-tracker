(ns karma-tracker-ui.views.format
  (:require [cljs-time.format :as format-time]))

(def full-month-and-year
  (partial format-time/unparse (format-time/formatter "MMMM YYYY")))

(def short-month
  (partial format-time/unparse (format-time/formatter "MMM")))

(defn percentage [value]
  (let [direction (cond
                    (pos? value) "↑ "
                    (neg? value) "↓ "
                    :else "")
        value (-> value Math/abs (* 100))
        rounded-value (condp <= value
                        10 (.toFixed value)
                        1 (.toFixed value 1)
                        "< 1")]
    (str direction rounded-value "%")))

(defn percentage-change [change vs]
  (str (if (zero? change)
         "Same as "
         (str (percentage change) " vs "))
       (short-month vs)))
