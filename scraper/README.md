# scraper

This is a spike using [Google BigQuery](https://cloud.google.com/bigquery/) to search the [GitHub Archive](https://www.githubarchive.org/) for open-source contributions made by members of a given organisation.

## Setup
You'll need a .lein-env file with credentials for both BigQuery and the GitHub API.
Copy .lein-env.example to .lein-env and fill in the blanks.

### BigQuery
Create a new project at the [Google IAM console](https://console.developers.google.com/iam-admin/projects), and enable the BigQuery API.
Create a set of credentials, choosing a "Service account key" and creating a new service account.
You will receive a JSON file with your details, which can be copied into .lein-env.

### GitHub
Create a new [personal access token](https://github.com/settings/tokens) with the `read:org` permission.

## Run

This is currently a command-line application.
You can run it with

```console
$ lein run organisation time-period
```

The `time-period` is a table name in the GitHub Archive, for example `year.2016`, `month.201504`, or `day.20130201`.
BigQuery only allows 1TB of free data processing per month, so stick to individual days unless you want to chew through that pretty quickly!

### Example

To fetch contributions from Badgers for 23 January,

```console
$ lein run redbadger day.20170123
({:user "samsmith1983",
  :event "DeleteEvent",
  :repo "samsmith1983/axis-medical",
  :details
  {:ref "23-jan-updates", :ref-type "branch", :pusher-type "user"},
  :time
  #object[org.joda.time.DateTime 0x42576db9 "2017-01-23T12:39:08.000Z"]}
 {:user "sheepsteak",
  :event "WatchEvent",
  :repo "jlongster/prettier-atom",
  :details {:action "started"},
  :time
  #object[org.joda.time.DateTime 0x1e477944 "2017-01-23T09:43:27.000Z"]}
  ...
```

## How it works

We're using Java interop to call the BigQuery client library.
Unfortunately, due to a [JVM bug](http://bugs.java.com/bugdatabase/view_bug.do?bug_id=4283544) dating back to 1999, we can't call the `setCredentials` method on `BigQueryOptions` from Clojure.
There's a little Java shim in `src/main/java` to construct the `BigQuery` client object, which is used as follows:

```clojure
(def bigquery
  (BigQueryBuilder/build (env :bigquery-project-id)
                         (env :bigquery-client-id)
                         (env :bigquery-client-email)
                         (env :bigquery-private-key)
                         (env :bigquery-private-key-id)))
```

Note that we are using the `environ` library to read configuration from the environment.

Once you have a client object, you can perform queries from Clojure by constructing a `QueryRequest`.
The following query returns all users who performed some action on 23 January:

```clojure
(def all-active-users-query (->
  (QueryRequest/newBuilder "SELECT DISTINCT actor.login FROM `githubarchive.day.20170123`")
  (.setUseLegacySql false)
  .build))
```

To execute the query, we invoke the `query` method on the `BigQuery` client object, returning a `QueryResponse`.
We can get the returned rows using the `result` method, and convert into a Clojure sequence by grabbing an iterator via `iterateAll` then using the built-in Clojure function `iterator-seq`:

```clojure
(def all-active-users-rows (->
  (.query bigquery all-active-users-query)
  .result
  .iterateAll
  iterator-seq))
```

The rows are lists of `FieldValue`s:

```clojure
([#object[com.google.cloud.bigquery.FieldValue 0xf14922e "FieldValue{attribute=PRIMITIVE, value=aferriss}"]]
 [#object[com.google.cloud.bigquery.FieldValue 0x4e73360a "FieldValue{attribute=PRIMITIVE, value=pjaaskel}"]]
 ...)
```

Converting them into a more useful form is a case of grabbing each field in each list, and calling the appropriate conversion method (e.g. `getStringValue` or `getTimestampValue`) to extract its value:

```clojure
(map #(.getStringValue (first %)) all-active-users-rows)

=> ("aferriss" "pjaaskel" "darylclimb" "SurrealSoul" "ekta1108" ...)
```

Queries can have named parameters.
The following function returns a query for all repos interacted with by the given user on 23 January:

```clojure
(defn user-activity-query [user]
  (->
    (QueryRequest/newBuilder "SELECT repo.name FROM `githubarchive.day.20170123` WHERE actor.login = @user")
    (.addNamedParameter "user" (QueryParameterValue/string user))
    (.setUseLegacySql false)
    .build))
```
