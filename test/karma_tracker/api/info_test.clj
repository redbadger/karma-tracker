(ns karma-tracker.api.info-test
  (:require [karma-tracker.api.info :refer :all]
            [clojure.test :refer :all]
            [karma-tracker.api
             [core :refer [wrap-handler]]
             [mock :refer [make-request]]]))

(defn- request-handler [execution-fn]
  (wrap-handler (info-routes execution-fn)))

(deftest retrieve-not-valid-info-test
  (let [request  (make-request :get "/api/info/not-valid-info")
        handler  (request-handler (fn [_]
                                    (throw (ex-info "Invalid request" {:info-request "not-valid-info"}))))
        response (handler request)]
    (is (= (:status response) 400))
    (is (= (:body response) "{\"info-request\":\"not-valid-info\",\"error\":\"Invalid request\"}"))))

(deftest retrieve-info-test
  (let [request  (make-request :get "/api/info/a-valid-info")
        handler  (request-handler (fn [request]
                                    (let [{{:keys [info-request]} :params} request]
                                      [:info 1 2 3 4])))
        response (handler request)]
    (is (= (:status response) 200))
    (is (= (:body response) "[\"info\",1,2,3,4]"))))
