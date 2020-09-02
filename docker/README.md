# Setup a Docker Enviroment

1. Create a `.env` file on this directory, using `.env-example` as reference, setting all the necessary environment variables.
2. Build containers' images: `docker compose build` (this might take several minutes).
3. Bring up the containers: `docker-compose up -d`.
4. If you used the default ports, go to http://localhost:9000 to access BIcenter web platform.

## TL;DR
1. Create a `.env` file on this directory, using `.env-example` as reference, setting all the necessary environment variables.
2. Run `sh run.sh`, present in the root of this repository.
3. If you used the default ports, go to http://localhost:9000 to access BIcenter web platform.
