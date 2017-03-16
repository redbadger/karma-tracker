(ns karma-tracker-ui.subs
  (:require [re-frame.core :as re-frame]
            [cljs-time.core :as time]
            [clojure.string :as string]))

(re-frame/reg-sub
 :initialized?
 (fn [db]
   (not= :initializing (:state db))))

(re-frame/reg-sub
 :error?
 (fn [db]
   (or (= :error (:state db))
       (and (= :loading (:state db))
            (= :error (:previous-state db))))))

(re-frame/reg-sub
 :current-date
 (fn [db]
   (:date db)))

(re-frame/reg-sub
 :previous-date
 (fn [db]
   (let [current-date (:date db)]
     (when current-date (time/minus current-date (time/months 1))))))

(re-frame/reg-sub
 :next-date
 (fn [db]
   (let [current-date (:date db)]
     (when current-date (time/plus current-date (time/months 1))))))

(re-frame/reg-sub
 :contribution-totals
 (fn [db]
   (-> db
       :contribution-totals
       (dissoc :total))))

(defn additional-repository-info [repository-name]
  (let [[owner name] (string/split repository-name #"/")]
    {:owner owner
     :name name
     :link (str "https://github.com/" owner "/" name)}))

(re-frame/reg-sub
 :repository-totals
 (fn [db [_ number]]
   (->> db
        :repositories
        (take number)
        (map (fn [[name values]] (merge (additional-repository-info name) values))))))

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
