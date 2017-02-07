(ns karma-tracker.events-test
  (:require [clojure
             [edn :as edn]
             [test :refer :all]]
            [clojure.java.io :as io]
            [karma-tracker.events :refer :all]))

(defn load-event [name]
  (-> (str name ".edn")
      io/resource
      io/file
      slurp
      edn/read-string))

(deftest issue-event-transform
  (testing "Normalize IssueEvent"
    (let [input-event (load-event "issue-event")
          expected-event {:type       :issue
                          :repo       "facebook/react"
                          :user       "gaearon"
                          :created-at "2017-01-26T15:50:20Z"}]
      (is (= expected-event (normalize input-event))))))

(deftest pr-comment-event-transform
  (testing "Transforms a Github PullRequestReviewCommentEvent into an event map"
    (let [input-event    (load-event "pr-comment-event")
          expected-event {:type       :pull-request-review-comment
                          :repo       "redbadger/karma-tracker"
                          :user       "mveritym"
                          :created-at "2017-01-30T11:35:09Z"}]
      (is (= expected-event (normalize input-event))))))

(deftest pr-event-transform
  (testing "Transforms a Github PullRequestEvent into an event map"
    (let [input-event    (load-event "pr-event")
          expected-event {:type       :pull-request
                          :repo       "facebook/react-devtools"
                          :user       "gaearon"
                          :created-at "2017-01-27T23:34:46Z"}]
      (is (= expected-event (normalize input-event))))))

(deftest push-event-normalization
  (testing "Normalize PushEvent"
    (let [input-event    (load-event "push-event")
          expected-event {:type       :push
                          :repo       "redbadger/blog-squarespace-template"
                          :user       "asavin"
                          :created-at "2017-02-06T17:09:51Z"
                          :commits 11}]
      (is (= expected-event (normalize input-event))))))

(deftest valid-types
  (testing "Defined types are valid"
    (let [events (->> types keys (map (fn [type] {:type type})))
          results (map valid? events)]
      (is (every? true? results))))
  (testing "Not defined type are not valid"
    (let [events (->> ["ForkEvent" "GistEvent" "ReleaseEvent"]
                      (map (fn [type] {:type type})))
          results (map valid? events)]
      (is (every? false? results)))))
