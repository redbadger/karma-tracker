(ns karma-tracker.update-tests
  (:require [karma-tracker.update :refer :all]
            [clojure.test :refer :all]
            [karma-tracker.github :as gh]
            [karma-tracker.events-repository :as events]))

(deftest update-events-test
  (let [organisation-performed-events-result (atom nil)
        events-add-result (atom nil)]
    (with-redefs [gh/organisation-performed-events (fn [& args]
                                                     (reset! organisation-performed-events-result
                                                             args)
                                                     [:event1 :event2 :event3])
                  events/add (fn [& args] (reset! events-add-result args))]
      (update-events :github :storage "redbadger")
      (is (= [:github "redbadger"]
             @organisation-performed-events-result))
      (is (= :storage (first @events-add-result)))
      (is (= [:event1 :event2 :event3] (second @events-add-result))))))
