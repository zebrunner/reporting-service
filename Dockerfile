FROM adoptopenjdk:11-jre-hotspot

ARG SERVICE_VER=1.0-SNAPSHOT

ENV VERSION=${SERVICE_VER}

RUN mkdir /opt/assets

COPY startup.sh /usr/local/bin/

RUN chmod +x /usr/local/bin/startup.sh

COPY ./web/build/libs/web-*.jar /app/reporting-service.jar

ENTRYPOINT ["startup.sh"]

EXPOSE 8080