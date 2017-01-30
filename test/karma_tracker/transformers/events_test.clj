(ns karma-tracker.transformers.events-test
  (:require [clojure.test :refer :all]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [karma-tracker.transformers.events :refer :all]))

(deftest issue-event-transform
  (testing "Transforms a Github IssueEvent into an event map"
    (let [input-file (-> "issue-event.edn" io/resource io/file)
          input-issue (-> input-file slurp edn/read-string)
          expected-event {:action "closed"
                          :type :issue
                          :repo {:name "facebook/react" :id 10270250}
                          :user {:login "gaearon" :id 810438}
                          :created-at "2017-01-26T15:50:20Z"}]
      (is (= expected-event (transform-to-event input-issue))))))

(deftest pr-event-transform
  (testing "Transforms a Github PullRequestEvent into an event map"
    (let [input-file (-> "pr-event.edn" io/resource io/file)
          input-pr   (-> input-file slurp edn/read-string)
          expected-pr {:action "closed"
                       :type :pullrequest
                       :repo {:name "facebook/react-devtools" :id 12601374}
                       :user {:login "gaearon" :id 810438}
                       :created-at "2017-01-27T23:34:46Z"}]
      (is (= expected-pr (transform-to-event input-pr))))))
