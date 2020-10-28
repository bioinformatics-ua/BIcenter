##########################################################
# Dockerfile to build BIcenter Platform
# Based on airdock/oracle-jdk:1.7
############################################################

# Set the base image to Ubuntu
FROM nimmis/java:oracle-8-jdk 
MAINTAINER Leonardo Coelho

# update and install unzip
RUN apt-get update
RUN apt-get install -y unzip

# Install hemospec
ADD pdi.zip /opt/pdi.zip
ADD *.jar /opt/
ADD config.sh /opt/config.sh
WORKDIR /opt
RUN chmod 777 config.sh
RUN bash config.sh

# setup symbolic links for persistent data

# Expose the default port
#EXPOSE 80

# add entrypoint to Docker
ADD slave-entrypoint.sh /opt/slave-entrypoint.sh
RUN chmod 777 /opt/slave-entrypoint.sh

# Set default container command
ENTRYPOINT ["/opt/slave-entrypoint.sh"]
