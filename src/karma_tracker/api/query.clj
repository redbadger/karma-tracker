(ns karma-tracker.api.query
  (:require [cheshire.core :refer [generate-string]]
            [clj-time
             [core :as t]
             [format :refer [formatter parse]]]
            [compojure.core :refer :all]
            [ring.util.response :refer :all]))

(def date-formatter (formatter "yyyy-MM-dd"))

(defn- params->query [params]
  (let [{:keys [start end source]} params]
    {:interval (t/interval (parse date-formatter start)
                           (parse date-formatter end))
     :source (keyword source)}))

(defn- request->query [{:keys [params] :as request}]
  (params->query params))

(defn- default-headers [request]
  (-> request
      (content-type "application/json")
      (header "Cache-Control" "max-age=86400; public")))

(defn- invalid-request-response [error]
  (-> {:error error}
      generate-string
      response
      (status 400)))

(defn- successful-query-response [query-result]
  (-> query-result
      generate-string
      response
      default-headers))

(defn- not-found-response [query-result]
  (-> query-result
      successful-query-response
      (status 404)))

(defn- query-response [query-result]
  (if (empty? query-result)
    (not-found-response query-result)
    (successful-query-response query-result)))

(defn- query-handler [query-execution-fn request]
  (try
    (-> request
        request->query
        query-execution-fn
        query-response)
    (catch RuntimeException e
      (invalid-request-response (.getMessage e)))))

(defn- parse-sources [sources]
  (if (sequential? sources)
    (set sources)
    (set (list sources))))

(defn- params->queries [params]
  (let [sources (-> params :s parse-sources)]
    (map (fn [source]
           (-> params
               (assoc :source source)
               params->query))
         sources)))

(defn- multi-query-handler [query-execution-fn {:keys [params] :as request}]
  (try
    (->> params
         params->queries
         (mapcat (fn [query]
                   [(:source query) (query-execution-fn query)]))
         (apply hash-map)
         query-response)
    (catch RuntimeException e
      (invalid-request-response (.getMessage e)))))

(defn query-routes [query-execution-fn]
  (routes
   (GET "/api/query/:start/:end" [] (partial multi-query-handler query-execution-fn))
   (GET "/api/query/:start/:end/:source" [] (partial query-handler query-execution-fn))))
