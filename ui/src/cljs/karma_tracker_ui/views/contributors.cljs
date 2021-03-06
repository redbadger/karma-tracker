(ns karma-tracker-ui.views.contributors
  (:require [re-frame.core :as re-frame]))

(defn contributor [{:keys [username avatar link]}]
  [:figure.contributor {:key username, :title username}
   [:a.contributor__link {:href link, :target "_blank"}
    [:img.contributor__avatar {:alt username, :src avatar}]]])

(defn contributors []
  (let [contributors (re-frame/subscribe [:contributors])]
    (fn []
      [:section.contributors
       [:h2 "Contributors"]
       [:p.explanation "Badgers who have made at least one contribution this month (in alphabetical order)"]
       (into [:div.row]
             (map #(vector contributor %) @contributors))])))
