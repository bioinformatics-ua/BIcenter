#!/bin/bash

set -e

function runInMasterMode {
	cd data-integration
	bash carte.sh $MASTER_IP $MASTER_PORT
}

function runInSlaveMode {
	cd data-integration
	bash carte.sh /opt/config.xml
}

if [ "$IS_MASTER" == "true" ]; then
	runInMasterMode
else
	runInSlaveMode
fi
