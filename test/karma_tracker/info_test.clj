(ns karma-tracker.info-test
  (:require [karma-tracker.info :refer :all]
            [clojure.test :refer :all]))

(deftest retrieve-request-invalid-test
  (is (thrown? RuntimeException (retrieve nil :not-existing-info))))
