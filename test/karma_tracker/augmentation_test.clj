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
    (let [conn     (gh/new-connection)
          result   (augment conn aggregation)
          expected (merge aggregation
                          {:repos-languages repo-languages-fake-response

                           :languages-chart
                           [{:item "Clojure", :value 50000, :percentage "43.6%"}
                            {:item "Elixir", :value 43000, :percentage "37.5%"}
                            {:item "Clojurescript", :value 20000, :percentage "17.4%"}
                            {:item "JavaScript", :value 1000, :percentage "0.9%"}
                            {:item "HTML", :value 500, :percentage "0.4%"}
                            {:item "Bash", :value 200, :percentage "0.2%"}]

                           :repos-contributions-chart
                           [{:item "hacker1/repo2", :value 4, :percentage "44.4%"}
                            {:item "hacker3/repo1", :value 3, :percentage "33.3%"}
                            {:item "hacker1/repo1", :value 2, :percentage "22.2%"}]

                           :overall-activity-chart
                           [{:item "Issues", :value 3, :percentage "33.3%"}
                            {:item "Commits", :value 3, :percentage "33.3%"}
                            {:item "Pull requests", :value 2, :percentage "22.2%"}
                            {:item "Pushes", :value 1, :percentage "11.1%"}]})]
      (doseq [key (keys expected)]
        (is (= (key result) (key expected)) (str "section: " key ", mismatched"))))))
