(ns karma-tracker.events-repository-test
  (:require [clojure.test :refer :all]
            [karma-tracker.events-repository :refer :all]
            [monger.db :as database]
            [monger.collection :as collection]
            [clj-time.core :refer [date-time local-date]]))

(def db (connect))

(defn clean-db [f]
  (collection/remove db events-collection)
  (f))

(use-fixtures :each clean-db)

(deftest inserts-new-events
  (add db [{:id 42, :actor "haines"}
           {:id 50, :actor "mveritym"}])
  (is (= [{:_id 42, :actor "haines"}
          {:_id 50, :actor "mveritym"}]
         (collection/find-maps db events-collection))))

(deftest updates-old-events
  (add db [{:id 42, :actor "haines"}
           {:id 50, :actor "mveritym"}])
  (add db [{:id 50, :actor "lazydevorg"}
           {:id 99, :actor "badgerbot"}])
  (is (= [{:_id 42, :actor "haines"}
          {:_id 50, :actor "lazydevorg"}
          {:_id 99, :actor "badgerbot"}]
         (collection/find-maps db events-collection))))

;; (deftest parses-date-times
;;   (add db [{:id 42
;;             :foo "2017-02-01T01:23:45Z"
;;             :nested {:bar "2000-01-01T00:00:00Z"}}])
;;   (is (= [{:_id 42
;;            :foo (date-time 2017 02 01 01 23 45)
;;            :nested {:bar (date-time 2000 01 01 00 00 00)}}]
;;          (collection/find-maps db events-collection))))

;; (deftest fetches-within-time-period
;;   (add db [{:id 11, :created_at "2016-12-31T23:59:59Z"}
;;            {:id 42, :created_at "2017-01-01T00:00:00Z"}
;;            {:id 50, :created_at "2017-12-31T23:59:59Z"}
;;            {:id 99, :created_at "2018-01-01T00:00:00Z"}])
;;   (is (= [{:id 42, :created_at (date-time 2017 01 01 00 00 00)}
;;           {:id 50, :created_at (date-time 2017 12 31 23 59 59)}]
;;          (fetch db (local-date 2017 01 01) (local-date 2017 12 31)))))

(deftest creates-index
  (let [index-keys (map :key (collection/indexes-on db events-collection))]
    (is (some #{{:created_at 1}} index-keys))))
