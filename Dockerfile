FROM maven:3.8.4-openjdk-17 AS build
LABEL maintainer="alexanderparpulansky@gmail.com"
WORKDIR /app
COPY pom.xml .
COPY src ./src


RUN mvn clean package -DskipTests



FROM eclipse-temurin:17
WORKDIR /app
COPY --from=build /app/target/cranker-0.0.1-SNAPSHOT.jar /app/cranker-docker.jar
ENTRYPOINT ["java", "-jar", "cranker-docker.jar"]