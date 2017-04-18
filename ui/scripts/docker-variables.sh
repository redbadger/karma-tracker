#!/usr/bin/env bash
set -e

export version="${1:-latest}"
export local_tag="karma-tracker/ui:${version}"
export registry="458707459747.dkr.ecr.eu-west-1.amazonaws.com"
export remote_tag="${registry}/${local_tag}"
