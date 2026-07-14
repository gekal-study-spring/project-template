#!/bin/sh
set -eu

exec java ${JAVA_OPTS:-} \
    -jar /app/migration.jar \
    "-environment=${ENV}" \
    -loggers=slf4j \
    "$@"
