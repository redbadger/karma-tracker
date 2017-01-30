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
                          :created_at "2017-01-26T15:50:20Z"}]
      (is (= expected-event (transform-to-event input-issue))))))
