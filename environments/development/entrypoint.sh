#!/bin/bash

BLUE="\033[34m"
NORMAL="\033[0;39m"

# run BIcenter with sbt
printf "\n\n${BLUE}BIcenter${NORMAL}\n"
cd BIcenter
find . -name target -type d -exec rm -rf {} \;
sbt run
