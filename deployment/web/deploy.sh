#!/bin/bash

docker stop bicenter

docker rm bicenter

docker rmi -f bicenter

docker build --force-rm=true --no-cache=true -t bicenter .

docker run -d -p 9091:9000 --name bicenter --link cartemaster:cartemaster --link mysql:mysql bicenter /bin/bash
