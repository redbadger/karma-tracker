![](doc/karma-logo.png)

# Karma Tracker
Open source contributions' tracker for organisations

[![CircleCI](https://circleci.com/gh/redbadger/karma-tracker.svg?style=svg&circle-token=b630e33db6bb4b0d3d3497a50233efdbbca2a7e3)](https://circleci.com/gh/redbadger/karma-tracker)

**NOTE: Karma Tracker is still work in progress and many more features and enhancement are planned to be release soon. Stay tuned!**

Karma Tracker generates contributions' reports to public projects by the developers of an organisation.
It uses the [GitHub events API v3](https://developer.github.com/v3/activity/events/) in order to track the
developers' activity and extract meaningful information.
The reports include information like: the top 20 projects, the top 10 languages, the types of contributions
and the developers involved. Each of them refers to a specific month of the year.

GitHub stores the last 300 events (or three months) for each developer so Karma Tracker stores them in a MongoDB database and read them later to create reports.

## Usage

To populate the database run a MongoDB instance (as explained later) and run the command
```shell
$ lein run update
```
when finished generate a report
```shell
$ lein run report 2017 1
```
the file `report-2017-1.html` will be created in the current directory.

## Running MongoDB

The app and tests need to connect to a MongoDB instance.
If you do not have one running locally, you can use the [official Docker image](https://hub.docker.com/_/mongo/):

```shell
$ docker-compose up -d
```

By default, the connection is to `mongodb://127.0.0.1:27017/karma-tracker`.
This may be overridden with the environment variable `MONGODB_URI`, which is of the form `mongodb://username:password@host:port/database`.
