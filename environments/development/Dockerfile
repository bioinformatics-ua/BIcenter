FROM openjdk:8
MAINTAINER Vasco Ramos

# install sbt
RUN \
  echo "deb https://dl.bintray.com/sbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list && \
  curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | apt-key add && \
  apt-get update && \
  apt-get install -y sbt nodejs npm

COPY development/entrypoint.sh /opt/dev/dev-entrypoint.sh
COPY publish-local-plugins.sh /opt/dev/publish-local-plugins.sh

WORKDIR /opt

RUN bash dev/publish-local-plugins.sh

ENTRYPOINT ["/bin/bash", "dev/dev-entrypoint.sh"]
