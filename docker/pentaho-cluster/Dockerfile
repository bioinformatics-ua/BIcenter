FROM openjdk:8
MAINTAINER Vasco Ramos

ARG PENTAHO_VERSION
ARG PDI_BUILD
ARG MYSQL_CONNECTOR_VERSION

# update and install unzip
RUN apt-get update && apt-get install -y unzip

COPY . /opt

WORKDIR /opt

# download pentaho
RUN \
	wget https://netix.dl.sourceforge.net/project/pentaho/Pentaho%20${PENTAHO_VERSION}/client-tools/pdi-ce-${PDI_BUILD}.zip && \
	mv pdi-ce-${PDI_BUILD}.zip pdi.zip && unzip pdi.zip

# download mysql-connector
RUN \
	wget -O mysql-connector.zip https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-java-${MYSQL_CONNECTOR_VERSION}.zip && \
	unzip mysql-connector.zip "*/*connector*bin.jar" && cp mysql-connector*/*.jar . && \
    mv *.jar data-integration/lib/ && chmod 664 data-integration/lib/*.jar

# Set default container command
ENTRYPOINT ["/bin/bash", "entrypoint.sh"]
