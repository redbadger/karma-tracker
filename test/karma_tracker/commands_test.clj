(ns karma-tracker.commands-test
  (:require [karma-tracker.commands :refer :all]
            [karma-tracker.update :refer [update-events]]
            [clojure.test :refer :all]))

(def fake-resources {:github-conn    (delay :gh)
                     :events-storage (delay :storage)})

(deftest execute-test
  (testing "Not defined command throw an exception"
    (is (thrown? Exception (execute fake-resources [:notimplemented 1 2 3]))))
  (testing "Update command"
    (let [update-events-result (atom nil)]
      (with-redefs [update-events (fn [& args]
                                    (reset! update-events-result args))]
        (is (= [:events-updated]
               (execute fake-resources [:update])))
        (is (= [:gh :storage "redbadger"]
               @update-events-result))))))
