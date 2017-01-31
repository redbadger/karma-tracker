(ns karma-tracker.events-repository-test
  (:require [clojure.test :refer :all]
            [karma-tracker.events-repository :as repo]
            [monger.db :as database]))

(def db (repo/connect))

(defn clean-db [f]
  (database/drop-db db)
  (f)
  (database/drop-db db))

(use-fixtures :each clean-db)

(deftest inserts-new-events
  (let [events [{:id 42 :actor "haines"} {:id 50 :actor "mveritym"}]]
    (repo/add db events)
    (is (= events (repo/fetch db)))))

(deftest updates-old-events
  (repo/add db [{:id 42 :actor "haines"} {:id 50 :actor "mveritym"}])
  (repo/add db [{:id 50 :actor "lazydevorg"} {:id 123 :actor "badgerbot"}])
  (is (= [{:id 42 :actor "haines"} {:id 50 :actor "lazydevorg"} {:id 123 :actor "badgerbot"}]
         (repo/fetch db))))
