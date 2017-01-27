(ns karma-tracker.cache-test
  (:require [clojure.test :refer :all]
            [karma-tracker.cache :refer :all]
            [clojure.java.io :as io]
            [environ.core :refer [env]]
            [clj-http.headers :refer [header-map]]))

(defn delete-cache-dir []
  (let [cache-dir (io/file (env :cache-dir))]
    (when (.exists cache-dir)
      (doseq [file (reverse (file-seq cache-dir))]
        (io/delete-file file)))))

(defn clean-cache [f]
  (delete-cache-dir)
  (f)
  (delete-cache-dir))

(use-fixtures :each clean-cache)

(defn times-42 [value] (* value 42))
(def times-42-with-cache (wrap-cache times-42))

(deftest wrapped-function-reads-cached-result
  (write-cache (cache-file 2) :cached-value)
  (is (= (times-42-with-cache 2) :cached-value)))

(deftest wrapped-function-writes-result-to-cache
  (is (= (times-42-with-cache 2) 84))
  (is (= (read-cache (cache-file 2)) 84)))

(deftest caching-preserves-case-insensitivity-of-http-headers
  (write-cache (cache-file :headers) (into (header-map) {"Content-Type" "application/json"}))
  (let [headers (read-cache (cache-file :headers))]
    (is (= "application/json" (get headers "content-type")))))
