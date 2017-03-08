(ns karma-tracker-ui.events
  (:require [re-frame.core :as re-frame]
            [karma-tracker-ui.db :as db]
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
