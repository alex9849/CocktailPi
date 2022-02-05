FROM openjdk:11-jdk-buster
EXPOSE 8080/tcp
ENV DOCKERIZE_VERSION v0.6.1

RUN wget https://github.com/jwilder/dockerize/releases/download/$DOCKERIZE_VERSION/dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && tar -C /usr/local/bin -xzvf dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && rm dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz

ENV LOG_LEVEL=debug
ENV TZ=Europe/Berlin
ENV DEMOMODE=true
ENV IS_RASPBERRY_PI=false
ENV DB_HOST=db
ENV DB_PORT=5432

COPY backend/target/server.jar /app.jar

CMD ["sh", "-c", "dockerize -wait tcp://${DB_HOST}:${DB_PORT} -timeout 300s -wait-retry-interval 30s java -jar /app.jar"]