#!/bin/bash

./download_pentaho.sh
cp pdi.zip carte-master/pdi.zip
cp pdi.zip carte-slave/pdi.zip

unzip mysql-connector.zip "*/*connector*bin.jar"
cp *.jar carte-master/ 
cp *.jar carte-slave/ 
 
docker-compose build
