FROM openjdk:25-ea-17-jdk-bookworm
EXPOSE 8080/tcp

RUN apt update
RUN apt -y full-upgrade
RUN apt -yq install python3-full python3-pip wget alsa-utils

ENV LOG_LEVEL=debug
ENV TZ=Europe/Berlin

COPY backend/target/server.jar /app.jar
COPY script/docker/application.properties /application.properties

ENTRYPOINT ["java","-jar","/app.jar"]
