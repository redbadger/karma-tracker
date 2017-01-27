(ns karma-tracker.transformers.events)

(def event-types {:IssuesEvent "issue"})

(defn get-event-type [type]
  (get event-types (keyword type)))

(defn transform-to-event [event]
  (let [event-type (-> event :type get-event-type)
        action     (-> event :payload :action)
        repoId     (-> event :repo :id)
        repoName   (-> event :repo :name)
        userId     (-> event :actor :id)
        userLogin  (-> event :actor :login)]
    {:type event-type
     :action action
     :repo {:id repoId :name repoName}
     :user {:id userId :login userLogin}}))
