(ns karma-tracker.transformers.events-test
  (:require [clojure.test :refer :all]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [karma-tracker.transformers.events :refer :all]))

(deftest issue-event-transform
  (testing "Transforms a Github IssueEvent into an event map"
    (let [input-file (-> "event-transformer.edn" io/resource io/file)
          input-issue (-> input-file slurp edn/read-string)
          expected-event {:action "closed"
                          :type "issue"
                          :repo {:name "facebook/react" :id 10270250}
                          :user {:login "gaearon" :id 810438}
                          :created-at "2017-01-26T15:50:20Z"}]
      (is (= expected-event (transform-to-event input-issue))))))

(deftest pr-comment-event-transform
  (testing "Transforms a Github PullRequestReviewCommentEvent into an event map"
    (let [input-file (-> "pr-comment-event.edn" io/resource io/file)
          input-pr-comment (-> input-file slurp edn/read-string)
          expected-pr-comment {:action "created"
                               :type :pull-request-comment
                               :repo {:name "redbadger/karma-tracker" :id 80008322}
                               :user {:login "mveritym" :id 1009524}
                               :created-at "2017-01-30T11:35:09Z"}]
      (is (= expected-pr-comment (transform-to-event input-pr-comment))))))
