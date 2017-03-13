#!/bin/sh -e

lein do clean, cljsbuild once production

brunch build --production

docker build --tag karma-tracker/ui:latest .
docker tag karma-tracker/ui:latest 458707459747.dkr.ecr.eu-west-1.amazonaws.com/karma-tracker/ui:latest
