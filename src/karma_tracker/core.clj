(ns karma-tracker.core
  (:require [tentacles.events :as events]
            [clj-http.client :as http]
            [karma-tracker.cache :refer [wrap-cache]])
  (:gen-class))

(defn demo-caching
  "All HTTP calls made inside the `http/with-middleware` function are cached.
  This code should result in 10 HTTP calls and hence 10 cache files being generated.
  A second call to the function will return much more quickly because the responses will be read from disk."
  []
  (http/with-middleware (conj http/default-middleware wrap-cache)
    (count (events/user-events "redbadger" {:all-pages true}))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
