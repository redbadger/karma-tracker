# Using BigQuery

The GitHub Archive, a dataset containing all public events since 2011, is available on Google BigQuery.
This means that we can use SQL to easily search for contributions by members of the organisation.

There is a proof-of-concept available on the [bigquery](https://github.com/redbadger/karma-tracker/tree/bigquery/scraper) branch.
This provides a function, `users-activity`, which retrieves events for the given users for a given time period.

```
(users-activity ["haines" "lazydevorg" "mveritym"] "day.20170126")
```

Time periods can be `year.2016`, `month.201504`, or `day.20130201`.
The list of users for a given organisation can be retrieved from the GitHub API using the Tentacles library.

For further details about how to use BigQuery from Clojure, please check out the [README](https://github.com/redbadger/karma-tracker/blob/bigquery/scraper/README.md).
