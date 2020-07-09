FROM adoptopenjdk:11-jre-hotspot

ARG SERVICE_VER=1.0-SNAPSHOT

ENV VERSION=${SERVICE_VER}

COPY startup.sh /usr/local/bin/

RUN chmod +x /usr/local/bin/startup.sh

COPY build/libs/reporting-service-*.jar /app/reporting-service.jar

ENTRYPOINT ["startup.sh"]

EXPOSE 8080