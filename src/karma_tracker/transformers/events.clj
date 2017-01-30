(ns karma-tracker.transformers.events)

(def event-types {:IssuesEvent "issue"})

(defn get-event-type [type]
  (get event-types (keyword type)))

(defn transform-to-event [event]
  (let [event-type (-> event :type get-event-type)
        action     (-> event :payload :action)
        repo-id     (-> event :repo :id)
        repo-name   (-> event :repo :name)
        user-id     (-> event :actor :id)
        user-login  (-> event :actor :login)
        created-at  (-> event :created_at)]
    {:type event-type
     :action action
     :repo {:id repo-id :name repo-name}
     :user {:id user-id :login user-login}
     :created_at created-at}))
