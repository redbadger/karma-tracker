(ns karma-tracker-ui.views.languages
  (:require [re-frame.core :as re-frame]
            [karma-tracker-ui.views.bar-chart :as bar-chart]))

(defn language [{:keys [name count]} bar-scale]
  [:div.language
   [:div.bar-chart__label
    name
    [:span.bar-chart__value count]]
   [:div.bar-chart__bar-container
    [:span.bar-chart__bar.language__bar {:style (bar-chart/bar-style count bar-scale)}]]])

(defn languages []
  (let [language-totals (re-frame/subscribe [:language-totals 10])]
    (fn []
      (let [bar-scale (/ 100 (-> @language-totals first :count))]
        [:section.languages
         [:h2 "Languages"]
         [:p.explanation "Number of repositories which contain each language"]
         (into [:div.bar-chart]
               (map #(language % bar-scale) @language-totals))]))))
