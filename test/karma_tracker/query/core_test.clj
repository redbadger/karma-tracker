(ns karma-tracker.query.core-test
  (:require [clj-time.core :refer [date-time]]
            [clojure
             [edn :as edn]
             [test :refer :all]]
            [clojure.java.io :as io]
            [karma-tracker
             [events-repository :as repo]
             [resources :refer [get-resources]]]
            [karma-tracker.query.core :refer :all]
            [monger.collection :as collection]
            [tentacles.repos :refer [languages]]))

(defn load-event [name]
  (-> (str name ".edn")
      io/resource
      io/file
      slurp
      edn/read-string))

(defn load-test-events []
  (map load-event ["issue-event" "pr-event" "push-event"]))

(defn db-setup [tests]
  (let [db (repo/connect)]
    (collection/remove db repo/events-collection)
    (collection/remove db "repos-languages-cache")
    (repo/add db (load-test-events)))
  (tests))

(use-fixtures :once db-setup)

(defn test-query [source]
  (new-query (date-time 2017 1 1)
             (date-time 2017 3 20)
             source))

(deftest execute-users-query-test
  (let [query (test-query :users)]
    (is (= ["asavin" "gaearon"]
           (execute (get-resources) query)))))

(deftest execute-contributions-query-test
  (let [query (test-query :contributions)]
    (is (= '({:item :commit, :value 11, :percentage "84.6%"}
             {:item :issue, :value 1, :percentage "7.7%"}
             {:item :pull-request, :value 1, :percentage "7.7%"}
             {:item :comment, :value 0, :percentage "0.0%"})
           (execute (get-resources) query)))))

(deftest execute-repos-contributions-query-test
  (let [query (test-query :repos-contributions)]
    (is (= '({:commit 11,
              :comment 0,
              :repo "redbadger/blog-squarespace-template",
              :total 11,
              :percentage "84.6%"}
             {:issue 1,
              :comment 0,
              :repo "facebook/react",
              :total 1,
              :percentage "7.7%"}
             {:pull-request 1,
              :comment 0,
              :repo "facebook/react-devtools",
              :total 1,
              :percentage "7.7%"})
           (execute (get-resources) query)))))

(deftest execute-languages-query-test
  (let [query (test-query :languages)]
    (with-redefs [languages (constantly {:Clojure       1000
                                         :Clojurescript 250
                                         :Javascript    100})]
      (is (= '({:item "Clojurescript", :value 3, :percentage "33.3%"}
               {:item "Javascript", :value 3, :percentage "33.3%"}
               {:item "Clojure", :value 3, :percentage "33.3%"})
             (execute (get-resources) query))))))

(deftest execute-repos-languages-query-test
  (let [query (test-query :repos-languages)]
    (with-redefs [languages (constantly {:Clojure       1000
                                         :Clojurescript 250
                                         :Javascript    100})]
      (is (= {"facebook/react"
              {:Clojure 1000, :Clojurescript 250, :Javascript 100},
              "facebook/react-devtools"
              {:Clojure 1000, :Clojurescript 250, :Javascript 100},
              "redbadger/blog-squarespace-template"
              {:Clojure 1000, :Clojurescript 250, :Javascript 100}}
             (execute (get-resources) query))))))

(deftest execute-unknown-query
  (let [query (test-query :not-existing-one)]
    (is (thrown? RuntimeException
                 (execute (get-resources) query)))))
