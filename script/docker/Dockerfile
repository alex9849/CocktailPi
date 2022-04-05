FROM openjdk:11-jdk-buster
EXPOSE 8080/tcp

RUN apt update
RUN apt -y full-upgrade
RUN apt -yq install python python3-pip wget alsa-utils

ENV LOG_LEVEL=debug
ENV TZ=Europe/Berlin
ENV DB_HOST=db
ENV DB_PORT=5432

COPY backend/target/server.jar /app.jar
COPY script/docker/application.properties /application.properties

ENTRYPOINT ["java","-jar","/app.jar"]
