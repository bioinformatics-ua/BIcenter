#!/bin/bash

rm -rf /opt/bicenter/RUNNING_PID

update-locale LANG=en_US.UTF-8

cd /opt/bicenter

bash bin/bicenter -Dhttp.port=9000 -Dfile.encoding=UTF-8 -J-XX:-UseSplitVerifier -J-XX:+CMSClassUnloadingEnabled
