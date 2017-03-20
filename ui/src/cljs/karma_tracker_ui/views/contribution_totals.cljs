(ns karma-tracker-ui.views.contribution-totals
  (:require [re-frame.core :as re-frame]
            [clojure.string :as string]
            [karma-tracker-ui.views.format :as format]))

(def types
  [:commits :pull-requests :issues :comments])

(defn map-totals [[component & args] totals]
  (map #(apply vector component % (% totals) args) types))

(defn contribution-total-class
  ([type] (str "contribution-total--" (name type)))
  ([type element] (str "contribution-total__" (name element) "--" (name type))))

(defn contribution-total [type values]
  (let [previous-date (re-frame/subscribe [:previous-date])]
    (fn [type {:keys [count change]}]
      (let [class (partial contribution-total-class type)]
        [:div.contribution-total {:class (class)}
         [:div.contribution-total__container {:class (class :container)}
          [:h3.contribution-total__type {:class (class :type)} (-> type name (string/replace "-" " "))]
          [:div.contribution-total__count {:class (class :count)} count]
          (when change
            [:div.contribution-total__change {:class (class :change)}
             (format/percentage-change change @previous-date)])]]))))

(defn contribution-totals []
  (let [contribution-totals (re-frame/subscribe [:contribution-totals])]
    (fn []
      [:section.contribution-totals
       [:h2.contribution-totals__heading "Red Badger open-source contributions"]
       (into [:div.row]
             (map-totals [contribution-total] @contribution-totals))])))
