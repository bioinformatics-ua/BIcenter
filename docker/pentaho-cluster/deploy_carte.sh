#!/bin/bash

# Download necessary sofware (Pentaho and MySQl connector)
sh "download_pentaho.sh"

echo ""

# Copy Pentaho zip to caDownload Pentaho if don't exist already
if [ ! -f "carte-master/pdi.zip" ]; then
    cp pdi.zip carte-master/pdi.zip
else
    echo "Pentaho (pdi.zip) alreay exists on carte-master"
fi


if [ ! -f "carte-slave/pdi.zip" ]; then
    cp pdi.zip carte-slave/pdi.zip
else
    echo "Pentaho (pdi.zip) alreay exists on carte-slave"
fi

if [ ! -d "mysql-connector-java-5.1.46" ]; then
    unzip mysql-connector.zip "*/*connector*bin.jar"
    cp mysql-connector-java-5.1.46/*.jar carte-master/
    cp mysql-connector-java-5.1.46/*.jar carte-slave/
else
    echo "MySQL connector 5.1.46 jar alreay exists"
fi

echo ""

# Build and run Pentaho related services
docker-compose up -d
