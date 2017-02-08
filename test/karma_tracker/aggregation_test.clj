(ns karma-tracker.aggregation-test
  (:require [karma-tracker.aggregation :refer :all]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.test :refer :all]))

(defn load-events []
  (-> "aggregation_test_events.edn"
      io/resource
      io/file
      slurp
      edn/read-string))

(deftest aggregation-functions-test
  (let [events (load-events)]
    (testing "Repositories set"
      (is (= (repos events)
             #{"hacker1/repo1" "hacker3/repo1" "hacker1/repo2"})))

    (testing "Users set"
      (is (= (users events)
             #{"hacker1" "hacker2" "hacker3"})))

    (testing "Users -> Repositories map"
      (is (= (users-repos events)
             {"hacker1" #{"hacker1/repo1" "hacker3/repo1"},
              "hacker2" #{"hacker1/repo1" "hacker3/repo1"},
              "hacker3" #{"hacker1/repo2"}})))

    (testing "Repositories -> Users map"
      (is (= (repos-users events)
             {"hacker1/repo1" #{"hacker2" "hacker1"},
              "hacker3/repo1" #{"hacker2" "hacker1"},
              "hacker1/repo2" #{"hacker3"}})))

    (testing "Overall activity stats"
      (is (= (overall-activity-stats events)
             {:pull-request 2, :issue 3, :push 1, :commits 3})))

    (testing "Repositories activity stats"
      (is (= (repos-activity-stats events)
             {"hacker1/repo2" {:push 1, :commits 3},
              "hacker1/repo1" {:pull-request 2},
              "hacker3/repo1" {:issue 3}})))

    (testing "Aggregation function"
      (is (= (aggregate events)
             {:overall-activity-stats
              {:pull-request 2, :issue 3, :push 1, :commits 3},
              :repos-activity-stats
              {"hacker1/repo2" {:push 1, :commits 3},
               "hacker1/repo1" {:pull-request 2},
               "hacker3/repo1" {:issue 3}},
              :repos #{"hacker1/repo2" "hacker1/repo1" "hacker3/repo1"},
              :users-repos
              {"hacker1" #{"hacker1/repo1" "hacker3/repo1"},
               "hacker2" #{"hacker1/repo1" "hacker3/repo1"},
               "hacker3" #{"hacker1/repo2"}},
              :repos-users
              {"hacker1/repo1" #{"hacker2" "hacker1"},
               "hacker3/repo1" #{"hacker2" "hacker1"},
               "hacker1/repo2" #{"hacker3"}},
              :users #{"hacker3" "hacker2" "hacker1"}})))))
