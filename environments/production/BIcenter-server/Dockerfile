FROM openjdk:8
MAINTAINER Vasco Ramos

RUN apt-get update && apt-get install -y unzip

COPY bicenter.zip /opt/bicenter.zip
WORKDIR /opt
RUN unzip bicenter.zip || true && mv bicenter-1.0.0-SNAPSHOT BIcenter

COPY conf /opt/BIcenter/conf

COPY entrypoint.sh entrypoint.sh

ENTRYPOINT ["/bin/bash", "entrypoint.sh"]
