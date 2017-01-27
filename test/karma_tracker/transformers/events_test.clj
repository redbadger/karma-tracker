(ns karma-tracker.transformers.events-test
  (:require [clojure.test :refer :all]
            [clojure.edn :as edn]
            [karma-tracker.transformers.events :refer :all]))

(deftest issue-event-transform
  (testing "Transforms a Github IssueEvent into an event map"
    (let [resource-str "test/karma_tracker/resources/event-transformer.edn"
          input-issue (edn/read-string (slurp resource-str))
          expected-event {:action "closed" :type "issue"}]
      (is (= expected-event (transform-to-event input-issue))))))
