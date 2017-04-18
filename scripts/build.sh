#!/usr/bin/env bash
set -e

source "${BASH_SOURCE%/*}/docker-variables.sh"

echo -e "\033[0;34mBuilding ${local_tag}\033[0m\n"

lein do clean, uberjar

docker build --tag "${local_tag}" .
docker tag "${local_tag}" "${remote_tag}"

echo -e "\n\033[0;32mSuccessfully built ${local_tag}\033[0m"
