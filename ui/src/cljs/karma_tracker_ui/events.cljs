(ns karma-tracker-ui.events
  (:require [re-frame.core :as re-frame]
            [karma-tracker-ui.api.overview :as overview-api]
            [karma-tracker-ui.db :as db]
            [karma-tracker-ui.routes :as routes]
            [cljs-time.core :as time]
            [cljs.spec :as spec]))

(defn check-spec
  [spec db]
  (when-not (spec/valid? spec db)
    (throw (ex-info (str "spec check failed: " (spec/explain-str spec db)) {}))))

(def check-spec-interceptor
  (re-frame/after (partial check-spec :karma-tracker-ui.db/db)))

(re-frame/reg-event-db
 :initialize-db
 [check-spec-interceptor]
 (fn  [_ _]
   db/default))

(re-frame/reg-event-fx
 :select-initial-view
 (fn [_ _]
   {:redirect (routes/overview (time/now))}))

(re-frame/reg-event-fx
 :select-overview
 [check-spec-interceptor]
 (fn [cofx [_ date]]
   {:db (merge (:db cofx)
               {:state :loading
                :date date})
    :http-xhrio (overview-api/request date)}))

(re-frame/reg-event-db
 :handle-overview-response
 [check-spec-interceptor]
 (fn [db [_ date response]]
   (if (= date (:date db))
     (merge db (overview-api/response response) {:state :ready})
     db)))

(re-frame/reg-event-db
 :handle-failed-request
 [check-spec-interceptor]
 (fn [db [_ result]]
   (.error js/console "API request failed:" result)
   (assoc db :state :error)))
