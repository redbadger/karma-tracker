(ns karma-tracker.commands
  (:require [environ.core :refer [env]]
            [karma-tracker.api.core :as api]
            [karma-tracker.update :refer [update-events]]))

(defmulti execute
  (fn [_ [command] & args]
    command))

(defmethod execute :default [_ command]
  (throw (ex-info "Command not implemented" {:command command})))

(defmacro with-db [& body]
  `(try
     ~@body
     (catch com.mongodb.MongoTimeoutException e#
       [:error "Could not connect to MongoDB"])))

(defmethod execute :update [{:keys [github-conn events-storage]} _]
  (with-db
    (update-events @github-conn @events-storage (env :organisation))
    [:events-updated]))

(defmethod execute :api [resources _]
  (api/run-server resources)
  [:server-stopped])
