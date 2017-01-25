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
