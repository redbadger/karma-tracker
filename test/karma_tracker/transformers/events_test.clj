(ns karma-tracker.transformers.events-test
  (:require [clojure.test :refer :all]
            [karma-tracker.transformers.events :refer :all]
            [karma-tracker.transformers.stubs :as stubs]))

(deftest issue-event-transform
  (testing "Transforms a Github IssueEvent into an event map"
    (let [input-issue stubs/issue-stub
          expected-event {:action "closed" :type "issue"}]
      (is (= expected-event (transform-to-event input-issue))))))
