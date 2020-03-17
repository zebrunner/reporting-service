FROM openjdk:11-jdk-slim

ARG version=1.0-SNAPSHOT
ARG SERVICE_VER=1.0-SNAPSHOT
ARG CLIENT_VER=1.0-SNAPSHOT

ENV ZAFIRA_VERSION=${SERVICE_VER} \
    ZAFIRA_CLIENT_VERSION=${CLIENT_VER}

RUN mkdir /opt/assets

COPY startup.sh /usr/local/bin/

RUN chmod +x /usr/local/bin/startup.sh

COPY ./web/build/libs/web-${version}.jar /app/reporting-service.jar

ENTRYPOINT ["startup.sh"]

EXPOSE 8080