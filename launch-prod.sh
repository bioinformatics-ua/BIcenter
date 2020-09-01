#!/bin/bash

# stop on first error
set -e

# copy conf file
cp -r conf environments/production/BIcenter-server

# copy project's distributable package generated by `sbt dist`
cp target/universal/bicenter-1.0.0-SNAPSHOT.zip environments/production/BIcenter-server/bicenter.zip

# launch production environment
cd environments/production/BIcenter-server && docker-compose up -d && cd ../../..

rm -rf environments/production/BIcenter-server/conf

