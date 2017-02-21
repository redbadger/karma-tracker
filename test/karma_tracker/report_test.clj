(ns karma-tracker.report-test
  (:require [karma-tracker.report :refer :all]
            [karma-tracker.events-repository :refer [get-events-for-month]]
            [karma-tracker.github :refer [repo-languages]]
            [clojure.test :refer :all]
            [clojure.java.io :as io]
            [clojure.edn :as edn]))

(defn load-event [name]
  (-> (str name ".edn")
      io/resource
      io/file
      slurp
      edn/read-string))

(defn get-test-events []
  (map load-event ["issue-event" "pr-comment-event" "pr-event" "push-event"]))

(def report-data {:repos-contributions-chart
                  [{:item "redbadger/blog-squarespace-template"
                    :value 12
                    :percentage "80.0%"}
                   {:item "facebook/react" :value 1 :percentage "6.7%"}
                   {:item "redbadger/karma-tracker" :value 1 :percentage "6.7%"}
                   {:item "facebook/react-devtools" :value 1 :percentage "6.7%"}]
                  :overall-activity-chart
                  [{:item "Commits" :value 11 :percentage "73.3%"}
                   {:item "Pushes" :value 1 :percentage "6.7%"}
                   {:item "Issues" :value 1 :percentage "6.7%"}
                   {:item "Reviews' comments" :value 1 :percentage "6.7%"}
                   {:item "Pull requests" :value 1 :percentage "6.7%"}]
                  :overall-activity-stats
                  {:issue 1
                   :pull-request-review-comment 1
                   :pull-request 1
                   :push 1
                   :commits 11}
                  :languages-chart
                  [{:item "Clojure" :value 40 :percentage "62.5%"}
                   {:item "Clojurescript" :value 20 :percentage "31.3%"}
                   {:item "Javascript" :value 4 :percentage "6.3%"}]
                  :repos-languages
                  {"facebook/react" {:Clojure 10 :Clojurescript 5 :Javascript 1}
                   "redbadger/karma-tracker"
                   {:Clojure 10 :Clojurescript 5 :Javascript 1}
                   "facebook/react-devtools"
                   {:Clojure 10 :Clojurescript 5 :Javascript 1}
                   "redbadger/blog-squarespace-template"
                   {:Clojure 10 :Clojurescript 5 :Javascript 1}}
                  :top-repos-users #{"gaearon" "mveritym" "asavin"}})

(deftest get-data-test
  (with-redefs [get-events-for-month (constantly (get-test-events))
                repo-languages       (constantly {:Clojure 10 :Clojurescript 5 :Javascript 1})]
    (let [result (get-data :gh :storage 2017 1)]
      (doseq [key (keys report-data)]
        (is (= (key report-data) (key result)) (str "Section: " key ", mismatched"))))))
