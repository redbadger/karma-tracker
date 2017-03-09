(ns karma-tracker-ui.views
  (:require [re-frame.core :as re-frame]
            [karma-tracker-ui.views.date-selector :refer [date-selector]]
            [karma-tracker-ui.views.contribution-totals :refer [contribution-totals]]
            [karma-tracker-ui.views.repositories :refer [repositories]]
            [karma-tracker-ui.views.languages :refer [languages]]
            [karma-tracker-ui.views.contributors :refer [contributors]]))

(defn root []
  (let [initialized? (re-frame/subscribe [:initialized?])]
    (fn []
      (when @initialized?
        [:div
         [date-selector]
         [:div.container
          [contribution-totals]
          [:div.row
           [repositories]
           [languages]]
          [contributors]]]))))
