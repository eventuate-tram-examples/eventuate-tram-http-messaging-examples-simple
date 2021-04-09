#! /bin/bash

set -e

dockerall="./gradlew ${DATABASE?}${MODE?}Compose"
dockercdc="./gradlew ${DATABASE?}${MODE?}cdcCompose"

${dockerall}Down
${dockercdc}Up

./gradlew -x :end-to-end-tests:test build

${dockerall}Up

./gradlew :end-to-end-tests:cleanTest :end-to-end-tests:test

${dockerall}Down
