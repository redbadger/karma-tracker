(ns karma-tracker-ui.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as re-frame]
            [cljs-time.core :as time]
            [clojure.string :as string]))

(re-frame/reg-sub
 :date
 (fn [db]
   (:date db)))

(re-frame/reg-sub
 :previous-date
 (fn [db]
   (-> db :date (time/minus (time/months 1)))))

(re-frame/reg-sub
 :next-date
 (fn [db]
   (-> db :date (time/plus (time/months 1)))))

(re-frame/reg-sub
 :contribution-totals
 (fn [db]
   (-> db
       :contribution-totals
       (dissoc :total))))

(defn split-repository-name [repository-name]
  (let [[owner name] (string/split repository-name #"/")]
    {:owner owner
     :name name}))

(re-frame/reg-sub
 :repository-totals
 (fn [db [_ number]]
   (->> db
        :repositories
        (take number)
        (map (fn [[name values]] (merge (split-repository-name name) values))))))

(re-frame/reg-sub
 :language-totals
 (fn [db [_ number]]
   (->> db
        :languages
        (take number)
        (map (fn [[name count]] {:name name :count count})))))

(re-frame/reg-sub
 :contributors
 (fn [db]
   (:contributors db)))
