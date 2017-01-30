(ns karma-tracker.cache
  (:require [clojure.java.io :as io]
            [taoensso.nippy :as nippy]
            [environ.core :refer [env]]
            [clj-http.headers :refer [header-map]])
  (:import [java.io DataInputStream DataOutputStream]
           [clj_http.headers HeaderMap]))

(nippy/extend-freeze HeaderMap :clj-http/header-map [headers out]
  (nippy/freeze-to-out! out (into (array-map) headers)))

(nippy/extend-thaw :clj-http/header-map [in]
  (into (header-map) (nippy/thaw-from-in! in)))

(defn cache-file
  "The file to read/write with the cached response for the request."
  [request]
  (io/file (env :cache-dir) (str (hash request))))

(defn read-cache
  "Reads data from the cache."
  [file]
  (with-open [in (-> file io/input-stream DataInputStream.)]
    (nippy/thaw-from-in! in)))

(defn write-cache
  "Writes data to the cache, returning the data itself."
  [file data]
  (do
    (io/make-parents file)
    (with-open [out (-> file io/output-stream DataOutputStream.)]
      (nippy/freeze-to-out! out data))
    data))

(defn wrap-cache
  "clj-http middleware handling caching."
  [client]
  (fn [request]
    (let [file (cache-file request)]
      (if (.exists file)
        (read-cache file)
        (write-cache file (client request))))))

(defmacro with-cache
  "Perform the body of the macro with HTTP caching enabled."
  [& body]
  `(http/with-middleware (conj http/*current-middleware* wrap-cache)
     ~@body))
