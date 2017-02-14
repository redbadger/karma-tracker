(ns karma-tracker.commands
  (:require [environ.core :refer [env]]
            [karma-tracker.update :refer [update-events]]))

(defmulti execute
  (fn [_ [command] & args]
    command))

(defmethod execute :default [_ command]
  (throw (ex-info "Command not implemented" {:command command})))

(defmethod execute :update [{:keys [github-conn events-storage]} _]
  (update-events @github-conn @events-storage (env :organisation))
  [:events-updated])

(defmethod execute :report [{:keys [github-conn events-storage]} [_ year month]]
  [:report-generated year month])
