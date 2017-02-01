![](doc/karma-logo.png)

# Karma Tracker
Open source contributions' tracker for organisations

[![CircleCI](https://circleci.com/gh/redbadger/karma-tracker.svg?style=svg&circle-token=b630e33db6bb4b0d3d3497a50233efdbbca2a7e3)](https://circleci.com/gh/redbadger/karma-tracker)

## Running MongoDB

The app and tests need to connect to a MongoDB instance.
If you do not have one running locally, you can use the [official Docker image](https://hub.docker.com/_/mongo/):

```shell
$ docker-compose up -d
```

By default, the connection is to `mongodb://127.0.0.1:27017/karma-tracker`.
This may be overridden with the environment variable `MONGODB_URI`, which is of the form `mongodb://username:password@host:port/database`.
