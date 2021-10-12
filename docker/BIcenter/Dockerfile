FROM openjdk:8
MAINTAINER Joao Almeida

# install sbt, nodejs, npm and unzip
RUN  apt-get update && apt-get install -y unzip

RUN curl -fsL https://github.com/sbt/sbt/releases/download/v1.5.5/sbt-1.5.5.tgz | tar xfz - -C /usr/share && \
    chown -R root:root /usr/share/sbt && \
    chmod -R 755 /usr/share/sbt && \
    ln -s /usr/share/sbt/conf /etc/sbt && \
    ln -s /usr/share/sbt/bin/java9-rt-export.jar /usr/bin/java9-rt-export.jar && \
    ln -s /usr/share/sbt/bin/sbt /usr/bin/sbt && \
    groupadd sbt --system && \
    useradd --gid sbt --system --shell /bin/false -c "sbt daemon-user" sbt


WORKDIR /opt

COPY entrypoint.sh entrypoint.sh
COPY publish-local-plugins.sh publish-local-plugins.sh

RUN bash publish-local-plugins.sh

RUN  apt-get upgrade

# Install nodejs
RUN apt install -y nodejs npm

CMD tail -f >> /dev/null

#ENTRYPOINT ["/bin/bash", "entrypoint.sh"]