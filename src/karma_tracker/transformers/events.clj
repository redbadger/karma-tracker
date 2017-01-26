(ns karma-tracker.transformers.events)

(def event-types {:IssuesEvent "issue"})

(defn get-event-type [type]
  ((keyword type) event-types))

(defn transform-to-event [event]
  (let [event-type (get-event-type (:type event))
        action (:action (:payload event))]
    {:type event-type :action action}))
