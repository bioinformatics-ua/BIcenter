#!/bin/bash

BLUE="\033[34m"
NORMAL="\033[0;39m"

printf "${BLUE}\nUpdate certificates${NORMAL}\n"
update-ca-certificates -f

# com.bmdsoftware/sbt-rjs;1.0.9
printf "\n\n${BLUE}SBT-RJS${NORMAL}\n"
git clone https://github.com/vascoalramos/sbt-rjs.git # FIXME: update to the main repo when new version is available
cd sbt-rjs && sbt publishLocal && cd ..

# com.bicou.sbt/sbt-hbs;1.0.8
printf "\n\n${BLUE}SBT-HBS${NORMAL}\n"
git clone https://github.com/bioinformatics-ua/sbt-hbs.git
cd sbt-hbs && sbt publishLocal

# add repositories into the file ~/.sbt/repositories
echo "[repositories]
local
sbt-releases-repo: https://repo.typesafe.com/typesafe/ivy-releases/, [organization]/[module]/(scala_[scalaVersion]/)(sbt_[sbtVersion]/)[revision]/[type]s/[artifact](-[classifier]).[ext]
sbt-plugins-repo: https://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/, [organization]/[module]/(scala_[scalaVersion]/)(sbt_[sbtVersion]/)[revision]/[type]s/[artifact](-[classifier]).[ext]
maven-central: https://repo1.maven.org/maven2/" >> ~/.sbt/repositories
