(ns karma-tracker.cache
  (:require [clj-time.core :as t]
            [environ.core :refer [env]]
            [monger
             [collection :as collection]
             [core :as mongo]]))

(defn mongodb-connect
  "Connects to MongoDB and returns the database client."
  ([] (mongodb-connect (env :mongodb-uri)))
  ([uri] (doto (:db (mongo/connect-via-uri uri)))))

(defn expired? [max-age {:keys [created-at] :as document}]
  (t/before? (t/plus created-at max-age) (t/now)))

(defn create-document [id value]
  {:_id id
   :value value
   :created-at (t/now)})

(defprotocol CacheProtocol
  (connect [cache])
  (write [cache id value])
  (read [cache id]))

(defrecord Cache [collection max-age]
  CacheProtocol

  (connect [cache]
    (assoc cache :db (mongodb-connect)))

  (write [{:keys [db]} id value]
    (collection/update db
                       collection
                       {:_id id}
                       (create-document id value)
                       {:upsert true})
    value)

  (read [{:keys [db]} id]
    (if-let [document (collection/find-one-as-map db
                                                  collection
                                                  {:_id id})]
      (when-not (expired? max-age document)
        (:value document)))))

(defn connection [collection max-age]
  (->Cache collection max-age))

(defn read-or-fetch [cache id fetch-fn]
  (if-let [document (read cache id)]
    document
    (write cache id (fetch-fn))))
