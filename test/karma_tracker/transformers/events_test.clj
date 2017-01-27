(ns karma-tracker.transformers.events-test
  (:require [clojure.test :refer :all]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [karma-tracker.transformers.events :refer :all]))

(deftest issue-event-transform
  (testing "Transforms a Github IssueEvent into an event map"
    (let [input-file (-> "event-transformer.edn" io/resource io/file)
          input-issue (-> input-file slurp edn/read-string)
          expected-event {:action "closed" :type "issue"}]
      (is (= expected-event (transform-to-event input-issue))))))
