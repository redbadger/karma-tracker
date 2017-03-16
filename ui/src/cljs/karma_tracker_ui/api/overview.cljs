(ns karma-tracker-ui.api.overview
  (:require [karma-tracker-ui.config :as config]
            [ajax.core :as ajax]
            [cljs-time.core :as time]
            [cljs-time.format :as format-time]))

(def format-date
  (partial format-time/unparse (format-time/formatters :date)))

(defn request [date]
  {:method :get
   :uri (str config/api-url
             "/query/"
             (-> date time/first-day-of-the-month format-date)
             "/"
             (-> date time/last-day-of-the-month format-date)
             "?s=contributions"
             "&s=repos-contributions"
             "&s=languages"
             "&s=users")
   :response-format (ajax/json-response-format {:keywords? true})
   :on-success [:handle-overview-response date]
   :on-failure [:handle-failed-request]})

(defn map-map [f coll]
  (->> coll
       (mapcat f)
       (apply array-map)))

(defn pluralize-keys [m]
  (->> m
       (map (fn [[k v]] [(-> k name (str "s") keyword) v]))
       (into {})))

(defn add-total [contribution-totals]
  (let [total-count (->> contribution-totals
                         vals
                         (map :count)
                         (reduce +))]
    (assoc contribution-totals :total {:count total-count})))

(defn contribution-total [{:keys [item value]}]
  [(keyword item) {:count value}])

(defn contribution-totals [{:keys [contributions]}]
  (->> contributions
       (map contribution-total)
       (into {})
       pluralize-keys
       add-total))

(defn repository [data]
  (let [defaults (zipmap [:commit :pull-request :issue :comment] (repeat 0))]
    [(:repo data) (as-> data $
                        (merge defaults $)
                        (select-keys $ (keys defaults))
                        (pluralize-keys $)
                        (map (fn [[k v]] [k {:count v}]) $)
                        (into {} $)
                        (add-total $))]))

(defn repositories [{:keys [repos-contributions]}]
  (map-map repository repos-contributions))

(defn language [{:keys [item value]}]
  [item value])

(defn languages [{:keys [languages]}]
  (map-map language languages))

(defn contributor [username]
  {:username username
   :avatar (str "https://github.com/" username ".png?size=240")
   :link (str "https://github.com/" username)})

(defn contributors [{:keys [users]}]
  (map contributor users))

(defn response [response]
  {:contribution-totals (contribution-totals response)
   :repositories (repositories response)
   :languages (languages response)
   :contributors (contributors response)})
