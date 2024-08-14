FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /tmp

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY ./ ./
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk-alpine

EXPOSE 9090

WORKDIR /api

COPY --from=build /tmp/target/*.jar ./quizstream-api.jar

COPY ./wait-for-it.sh ./wait-for-it.sh
RUN apk add --no-cache bash
RUN chmod +x wait-for-it.sh

ENTRYPOINT ["/bin/sh", "-c", "./wait-for-it.sh postgres:5432 --timeout=60 -- java -jar quizstream-api.jar --server.port=9090"]