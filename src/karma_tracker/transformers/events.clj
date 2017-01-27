(ns karma-tracker.transformers.events)

(def event-types {:IssuesEvent "issue"})

(defn get-event-type [type]
  (get event-types (keyword type)))

(defn transform-to-event [event]
  (let [event-type (-> event :type get-event-type)
        action     (-> event :payload :action)]
    {:type event-type :action action}))
