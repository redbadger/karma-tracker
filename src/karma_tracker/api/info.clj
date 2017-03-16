(ns karma-tracker.api.info
  (:require [compojure.core :refer :all]
            [ring.util.response :refer :all]
            [cheshire.core :refer [generate-string]]))

(defn- default-headers [request]
  (-> request
      (content-type "application/json")
      (header "Cache-Control" "max-age=86400; public")))

(defn- info-response [info-request retrieve-info-fn]
  (-> info-request
      retrieve-info-fn
      generate-string
      response
      default-headers))

(defn- invalid-info-response [ex]
  (-> ex
      ex-data
      (merge {:error (.getMessage ex)})
      generate-string
      response
      default-headers
      (status 400)))

(defn- info-request-handler [retrieve-info-fn request]
  (let [info-request (-> request :params :info-request keyword)]
    (try
      (info-response info-request retrieve-info-fn)
      (catch RuntimeException ex
        (invalid-info-response ex)))))

(defn info-routes [retrieve-info-fn]
  (routes
   (GET "/api/info/:info-request" [] (partial info-request-handler retrieve-info-fn))))
