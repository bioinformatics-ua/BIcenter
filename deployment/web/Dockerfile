############################################################
# Dockerfile to build BIcenter Platform
# Based on airdock/oracle-jdk:1.7
############################################################

# Set the base image to Ubuntu
FROM nimmis/java:oracle-8-jdk 
MAINTAINER Luis Bastiao Silva <bastiao@bmd-software.com>
MAINTAINER Renato Rodrigues 

# update and install unzip
RUN apt-get update
RUN apt-get install -y unzip

# Install hemospec
ADD bicenter.zip /opt/bicenter.zip
WORKDIR /opt
RUN unzip bicenter.zip

# setup symbolic links for persistent data

# Expose the default port
EXPOSE 9000

# add run.sh to Docker
ADD ./entrypoint.sh /opt/entrypoint.sh

# Set default container command
ENTRYPOINT ["/opt/entrypoint.sh"]
