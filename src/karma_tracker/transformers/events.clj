(ns karma-tracker.transformers.events)

(def types {"PullRequestEvent"              :pull-request
            "PullRequestReviewEvent"        :pull-request-review
            "PullRequestReviewCommentEvent" :pull-request-review-comment
            "IssueEvent"                    :issue
            "IssueCommentEvent"             :issue-comment})

(defn get-type [type]
  (get types type))

(def valid?
  (let [valid-types (-> types keys set)]
    #(contains? valid-types (:type %))))

(defn normalize [event]
  (let [type (-> event :type get-type)
        action     (-> event :payload :action)
        repo-id     (-> event :repo :id)
        repo-name   (-> event :repo :name)
        user-id     (-> event :actor :id)
        user-login  (-> event :actor :login)
        created-at  (-> event :created_at)]
    {:type type
     :action action
     :repo {:id repo-id :name repo-name}
     :user {:id user-id :login user-login}
     :created-at created-at}))

(def transform
  (comp (filter valid?)
        (map normalize)))
