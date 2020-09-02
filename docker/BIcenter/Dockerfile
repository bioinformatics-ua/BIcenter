FROM openjdk:8
MAINTAINER Vasco Ramos

# install sbt, nodejs, npm and unzip
RUN \
  echo "deb https://dl.bintray.com/sbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list && \
  curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | apt-key add && \
  apt-get update && apt-get install -y sbt nodejs npm unzip

WORKDIR /opt

COPY entrypoint.sh entrypoint.sh
COPY publish-local-plugins.sh publish-local-plugins.sh

RUN bash publish-local-plugins.sh

ENTRYPOINT ["/bin/bash", "entrypoint.sh"]
