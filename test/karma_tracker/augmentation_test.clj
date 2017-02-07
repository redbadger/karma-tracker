(ns karma-tracker.augmentation-test
  (:require [clojure.test :refer :all]
            [karma-tracker
             [augmentation :refer :all]
             [github :as gh]]
            [tentacles.repos :as repos]))

(def aggregation
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
   :users #{"hacker3" "hacker2" "hacker1"}})

(def repo-languages-fake-response {"hacker1/repo1" {:JavaScript 1000
                                                    :HTML       500}
                                   "hacker1/repo2" {:Clojure       50000
                                                    :Clojurescript 20000}
                                   "hacker3/repo1" {:Elixir 43000
                                                    :Bash   200}})

(defn repo-languages-fake-fn [user repo _]
  (get repo-languages-fake-response (str user "/" repo)))

(deftest augmentation-functions-test
  (with-redefs [repos/languages repo-languages-fake-fn]
    (let [conn   (gh/new-connection)
          result (augment conn aggregation)]
      (is (= result
             (merge aggregation
                    {:repos-languages repo-languages-fake-response

                     :languages-chart
                     [[:Clojure [50000 43.59197907585004]]
                      [:Elixir [43000 37.48910200523104]]
                      [:Clojurescript [20000 17.43679163034002]]
                      [:JavaScript [1000 0.8718395815170008]]
                      [:HTML [500 0.4359197907585004]]
                      [:Bash [200 0.1743679163034002]]]

                     :repos-contributions-chart
                     [["hacker1/repo2" [4 44.44444444444444]]
                      ["hacker3/repo1" [3 33.33333333333333]]
                      ["hacker1/repo1" [2 22.22222222222222]]]

                     :overall-activity-chart
                     [[:issue [3 33.33333333333333]]
                      [:commits [3 33.33333333333333]]
                      [:pull-request [2 22.22222222222222]]
                      [:push [1 11.11111111111111]]]}))))))
