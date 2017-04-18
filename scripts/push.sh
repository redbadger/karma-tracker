#!/usr/bin/env bash
set -e

source "${BASH_SOURCE%/*}/docker-variables.sh"

echo -e "\033[0;34mPushing ${local_tag} to ${registry}\033[0m\n"

docker push "${remote_tag}"

echo -e "\n\033[0;32mSuccessfully pushed ${local_tag} to ${registry}\033[0m"
