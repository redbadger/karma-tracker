(ns karma-tracker.api.query-test
  (:require [clj-time.core :refer [date-time interval]]
            [clojure.test :refer :all]
            [karma-tracker.api
             [core :refer [wrap-handler]]
             [mock :refer [make-request]]
             [query :refer :all]]))

(defn- request-handler [execution-fn]
  (wrap-handler (query-routes execution-fn)))

(defn- handler [queries-atom execution-fn]
  (request-handler (fn [query]
                     (swap! queries-atom conj query)
                     (execution-fn query))))

(defn- process-request [request execution-fn]
  (let [queries (atom [])
        process (handler queries execution-fn)]
    [queries (process request)]))


(deftest query-handler-test
  (let [request            (make-request :get "/api/query/2017-01-01/2017-03-31/languages")
        [queries response] (process-request request (constantly "fake query response"))]
    (is (= (count @queries) 1))
    (let [query (first @queries)]
      (is (= (:source query) :languages))
      (is (= (:interval query) (interval (date-time 2017 1 1)
                                         (date-time 2017 3 31)))))
    (is (= (:status response) 200))
    (is (= (:body response) "\"fake query response\""))
    (is (= (:headers response) {"Content-Type"  "application/json",
                                "Cache-Control" "max-age=86400; public"}))))

(deftest multi-query-handler-test
  (let [request (make-request :get "/api/query/2017-01-01/2017-03-31?s=users&s=contributions")
        [queries response] (process-request request (constantly "multiple fake query response"))
        query-interval (interval (date-time 2017 1 1)
                                 (date-time 2017 3 31))]
    (is (= @queries [{:source :users :interval query-interval}
                     {:source :contributions :interval query-interval}]))
    (is (= (:body response) "{\"contributions\":\"multiple fake query response\",\"users\":\"multiple fake query response\"}"))))
