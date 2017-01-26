# How to Use Tentacles – Examples

[Tentacles](https://github.com/Raynes/tentacles) is a Clojure library for working with the Github v3 API. Here are some 
example helper functions that use the library to fetch information about organizations, events, users, and rate limits.

## Adding Tentacles to the project

In the `project.clj` file we've added Tentacles to our project dependencies. Due to an issue with Clojure 1.8.0, we must also
use [jitpack](https://jitpack.io/).
```
:repositories [["jitpack" "https://jitpack.io"]]
:dependencies [[org.clojure/clojure "1.8.0"]
               [com.github.raynes/tentacles "0e16d9f"]]
```

## Orgs

Functions for accessing information about orgs are available under `tentacles.orgs`:
```
(ns open-source-tracker.core
  (:require [tentacles.orgs :as orgs]))
```

The following function counts the number of members in an organization given the org's name:
```
(defn get-num-org-members [org & [opts]]
  (count (orgs/members org opts)))
  
(get-num-org-members "redbadger")
=> 14
```
Without authentication `orgs/members` will return only public members of an organization. To add authentication, pass a map of options containing an `:auth` keyword as the last argument. You can also add other options like `:per-page` (number of results per page) and `:all-pages` (if true, returns all available pages):
```
(get-num-org-members "redbadger" {:auth "mveritym:PASSORAUTHTOKEN" :per-page 100 :all-pages true})
=> 74
```

Accessing information about each member is as simple as querying keys in the returned member map:
```
(defn get-member-names [org & [opts]]
  (map :login (orgs/members org opts)))

(get-member-names "redbadger" {:per-page 3})
=> ("asavin" "charypar" "dwynne")
```

A more general version:
```
(defn get-member-prop [org prop & [opts]]
  (map prop (orgs/members org opts)))
  
(get-member-prop "redbadger" :id {:per-page 3})
=> (487468 1517854 188308)
```

## Events

Functions for accessing information about events are available under `tentacles.events`:
```
(ns open-source-tracker.core
  (:require [tentacles.events :as events]))
```

For our purposes, we are most interested in events the user has performed: opening pull requests, commenting on issues, etc. We can get these with `events/performed-events`. Map over the returned sequence of events to get specific information about each event:
```
(defn get-all-events [member & [opts]]
  (let [events (events/performed-events member opts)]
    (map :created_at events)))
    
(get-all-events "mveritym" {:per-page 3})
=> ("2016-12-09T11:51:42Z" "2016-12-09T11:48:09Z" "2016-12-09T11:44:10Z")
```

Filter by event type to narrow your focus to pull requests, issues, or [another type](https://developer.github.com/v3/activity/events/types/) of event:
```
(defn get-events-by-type [user event-type & [opts]]
  (let [events (events/performed-events user opts)]
    (filter #(= (:type %) event-type) events)))
    
(get-events-by-type "mveritym" "PullRequestEvent" {:auth auth-token :per-page 1})
=> ({:id "5217913208",
     :type "PullRequestEvent",
     ...
```

Putting it all together! We can create a sequence of maps with the org's members' logins and the number of public pull request events they've made over the last three months (a Github limit). Authenticate to include private users too.
```
(defn get-num-prs [member & [opts]]
  (count (get-events-by-type member "PullRequestEvent" opts)))

(defn get-oss-contribs [org & opts]
  (let [members (get-member-names org (first opts))]
    (->> members
         (map #(hash-map :name % :num-prs (get-num-prs % (first opts)))))))
         
(get-oss-contribs "redbadger" {:per-page 5 :auth auth-token})
=> ({:num-prs 12, :name "ajcumine"}
    {:num-prs 0, :name "alhicks"}
    {:num-prs 0, :name "AmyBadger"}
    {:num-prs 18, :name "AndreiDanielIonescu"}
    {:num-prs 48, :name "AndrewBestbier"})
```
## Rate Limit
Although Tentacles doesn't have a specific method for fetching information about the rate-limit we can use its core `api-call` function to make a safe request to the rate-limit endpoint.
```
(ns open-source-tracker.core
  (:require [tentacles.core :as tcore]))
  
(defn get-rate-limit [& [opts]]
  (tcore/api-call :get "rate_limit" [] opts))
  
(get-rate-limit {:auth auth-token})
=> {:resources
      {:core {:limit 5000, :remaining 4796, :reset 1485447255},
       :search {:limit 30, :remaining 30, :reset 1485445591},
       :graphql {:limit 200, :remaining 200, :reset 1485449131}},
    :rate {:limit 5000, :remaining 4796, :reset 1485447255}}
```
Different parts of the API have slightly different rate limits and you'll get many more requests per hour if you authenticate
your API calls.

---
There's a lot more we can do with Tentacles but use these examples to point yourself in the right direction 👉
