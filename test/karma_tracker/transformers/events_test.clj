(ns karma-tracker.transformers.events-test
  (:require [clojure.test :refer :all]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [karma-tracker.transformers.events :refer :all]))

(deftest issue-event-transform
  (testing "Transforms a Github IssueEvent into an event map"
    (let [input-file (-> "issue-event.edn" io/resource io/file)
          input-issue (-> input-file slurp edn/read-string)
          expected-event {:type :issue
                          :repo {:name "facebook/react"
                                 :url "https://api.github.com/repos/facebook/react"}
                          :user {:login "gaearon"
                                 :url "https://api.github.com/users/gaearon"}
                          :created-at "2017-01-26T15:50:20Z"}]
      (is (= expected-event (normalize input-issue))))))

(deftest pr-comment-event-transform
  (testing "Transforms a Github PullRequestReviewCommentEvent into an event map"
    (let [input-file (-> "pr-comment-event.edn" io/resource io/file)
          input-pr-comment (-> input-file slurp edn/read-string)
          expected-pr-comment {:action "created"
                               :type :pull-request-comment
                               :repo {:name "redbadger/karma-tracker" :id 80008322}
                               :user {:login "mveritym" :id 1009524}
                               :created-at "2017-01-30T11:35:09Z"}]
      (is (= expected-pr-comment (normalize input-pr-comment))))))

(deftest pr-event-transform
  (testing "Transforms a Github PullRequestEvent into an event map"
    (let [input-file (-> "pr-event.edn" io/resource io/file)
          input-pr   (-> input-file slurp edn/read-string)
          expected-pr {:action "closed"
                       :type :pullrequest
                       :repo {:name "facebook/react-devtools" :id 12601374}
                       :user {:login "gaearon" :id 810438}
                       :created-at "2017-01-27T23:34:46Z"}]
      (is (= expected-pr (normalize input-pr))))))
