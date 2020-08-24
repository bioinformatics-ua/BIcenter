FROM openjdk:8
MAINTAINER Vasco Ramos

ENV SBT_VERSION 0.13.18

# install sbt
RUN \
  echo "deb https://dl.bintray.com/sbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list && \
  curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | apt-key add && \
  apt-get update && \
  apt-get install sbt

COPY . /opt/BIcenter

WORKDIR /opt

# UNCOMMENT THIS SECTION IF IS TO REVERT THE ONLINE PLUGIN PUBLISHMENT
# RUN chmod 777 "BIcenter/publish-local-plugins.sh"
# RUN "/opt/BIcenter/publish-local-plugins.sh"

RUN chmod 777 BIcenter/dev-entrypoint.sh

ENTRYPOINT ["/opt/BIcenter/dev-entrypoint.sh"]
