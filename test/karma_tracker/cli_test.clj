(ns karma-tracker.cli-test
  (:require [karma-tracker.cli :refer :all]
            [clojure.test :refer :all]))

(deftest parse-args-test
  (testing "Update command"
    (is (= [:update]
           (parse-args ["update"]))))
  (testing "Report with right data"
    (is (= [:report 2017 1]
           (parse-args ["report" "2017" "01"]))))
  (testing "Report with wrong data"
    (is (= [:error "The year or the month are not valid."]
           (parse-args ["report" "2017" "a"]))))
  (testing "Unknown command"
    (is (= [:unknown "whatACommand"]
           (parse-args ["whatACommand" "1-2-3"])))))

(deftest execute-test
  (testing "Default command execution"
    (let [execution-result (atom nil)
          execution-fn (fn [& args] (reset! execution-result args))]
      (execute execution-fn [:update])
      (is (= (list [:update]) @execution-result))))
  (testing "Unknown command"
    (is (= [:error "Command newcmd not valid"]
           (execute nil [:unknown "newcmd"]))))
  (testing "Execution error"
    (is (= [:error "Some bad things happened"]
           (execute nil [:error "Some bad things happened"])))))

(deftest show-result-test
  (testing "Default shows nothing"
    (is (= "" (with-out-str (show-result [:unknown-event])))))
  (testing "Events updated"
    (is (= "Events updated succesfully\n"
           (with-out-str (show-result [:events-updated])))))
  (testing "Report generated"
    (is (= "Report generated for 1 2017\n"
           (with-out-str (show-result [:report-generated 2017 1]))))))
