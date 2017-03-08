#!/bin/sh -e

lein do clean, cljsbuild once production

brunch build --production

docker build .
