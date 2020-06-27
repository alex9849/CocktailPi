FROM openjdk:8-jdk-alpine
EXPOSE 8080/tcp
ENV LOG_LEVEL=debug
ENV TZ=Europe/Berlin
RUN apk --no-cache add tzdata

COPY target/backend-1.0-SNAPSHOT.jar /app.jar

ENTRYPOINT ["java","-jar","/app.jar"]