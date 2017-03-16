(ns karma-tracker.info
  (:require [karma-tracker.events-repository :refer [get-available-months]]))

(defmulti retrieve
  (fn [resources info-request]
    info-request))

(defmethod retrieve :available-months [{:keys [events-storage]} _]
  (get-available-months @events-storage))

(defmethod retrieve :default [_ info-request]
  (throw (ex-info "Info request not valid" {:info-request info-request})))
