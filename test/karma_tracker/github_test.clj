(ns karma-tracker.github-test
  (:require [clojure.test :refer :all]
            [karma-tracker.github :refer :all]
            [tentacles
             [events :as events]
             [orgs :as orgs]
             [repos :as repos]]))

(defn fake-call [& args] args)

(deftest github-client-test
  (let [conn (new-connection "hacker" "H4CK3RT0CK3N" {})
        {{:keys [auth per-page all-pages] :as opts} :opts} conn]

    (testing "connection initialized"
      (is (= auth "hacker:H4CK3RT0CK3N"))
      (is (= per-page 100))
      (is (= all-pages true)))

    (testing "organization members api call"
      (with-redefs [orgs/members fake-call]
        (is (= ["redbadger" opts] (organisation-members conn "redbadger")))))

    (testing "user performed events api call"
      (with-redefs [events/performed-events fake-call]
        (is (= ["hacker" opts] (performed-events conn "hacker")))))

    (testing "repository languages"
      (with-redefs [repos/languages fake-call]
        (is (= ["hacker" "magic-app" opts] (repo-languages conn "hacker" "magic-app")))))))

(deftest organisation-performed-events-test
  (with-redefs [orgs/members            (fn [& args]
                                          [{:login "hacker1"}
                                           {:login "hacker2"}])
                events/performed-events (fn [login _]
                                          [(str "event1 " login)
                                           (str "event2 " login)])]
    (let [conn (new-connection "hacker" "H4CK3RT0CK3N" {})]
      (organisation-performed-events conn "redbadger")
      (is (= (organisation-performed-events conn "redbadger")
             '("event1 hacker1" "event2 hacker1" "event1 hacker2" "event2 hacker2"))))))
