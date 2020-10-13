FROM openjdk:8-jdk-buster
EXPOSE 8080/tcp
ENV LOG_LEVEL=debug
ENV TZ=Europe/Berlin

COPY backend/target/backend-*.jar /app.jar
COPY docker/lib/pi4j-1.2.deb /
RUN dpkg -i pi4j-1.2.deb

ENTRYPOINT ["java","-jar","/app.jar"]