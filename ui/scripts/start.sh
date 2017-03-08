#!/bin/sh -e

lein cljsbuild once development

concurrently --kill-others \
             --prefix name \
             --names 'brunch   ',cljsbuild \
             'brunch watch --server' \
             'lein cljsbuild auto development'
