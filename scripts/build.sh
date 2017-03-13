#!/bin/sh -e

lein do clean, uberjar

docker build --tag karma-tracker/cli:latest .
docker tag karma-tracker/cli:latest 458707459747.dkr.ecr.eu-west-1.amazonaws.com/karma-tracker/cli:latest
