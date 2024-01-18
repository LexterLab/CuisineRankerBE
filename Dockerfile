FROM eclipse-temurin:17 AS build
LABEL maintainer="alexanderparpulansky@gmail.com"
WORKDIR /app
COPY . /app


RUN apt-get update && apt-get install -y maven


RUN mvn clean package -DskipTests

FROM eclipse-temurin:17
WORKDIR /app
COPY --from=build /app/target/cranker-0.0.1-SNAPSHOT.jar /app/cranker-docker.jar
ENTRYPOINT ["java", "-jar", "cranker-docker.jar"]