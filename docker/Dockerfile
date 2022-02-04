FROM openjdk:11-jdk-buster
EXPOSE 8080/tcp
ENV LOG_LEVEL=debug
ENV TZ=Europe/Berlin
ENV DEMOMODE=false
ENV IS_RASPBERRY_PI=false

COPY backend/target/server.jar /app.jar

ENTRYPOINT ["java","-jar","/app.jar"]