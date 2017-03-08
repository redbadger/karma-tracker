(ns karma-tracker-ui.views.repositories
  (:require [re-frame.core :as re-frame]
            [karma-tracker-ui.views.contribution-totals :refer [map-totals]]
            [karma-tracker-ui.views.bar-chart :as bar-chart]))

(defn repository-bar-segment [type {:keys [count]} scale]
  [:span.bar-chart__bar.repository__bar {:class (str "repository__bar--" (name type))
                                         :style (bar-chart/bar-style count scale)}])

(defn repository [repository bar-scale]
  [:div.repository
   [:div.bar-chart__label
    [:span.repository__owner (:owner repository)]
    [:span.repository__name (:name repository)]
    [:span.bar-chart__value (-> repository :total :count)]]
   (into [:div.bar-chart__bar-container]
         (map-totals [repository-bar-segment bar-scale] repository))])

(defn repositories []
  (let [repository-totals (re-frame/subscribe [:repository-totals 10])]
    (fn []
      (let [bar-scale (/ 100 (-> @repository-totals first :total :count))]
        [:section.repositories
         [:h2 "Repositories"]
         [:p.explanation "Number of contributions to each repository"]
         (into [:div.bar-chart]
               (map #(repository % bar-scale) @repository-totals))]))))
