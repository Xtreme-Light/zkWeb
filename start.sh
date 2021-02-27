#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

nohup java -jar zkweb-0.0.1-SNAPSHOT.jar --server.port=9090 --spring.profile.active=dev &