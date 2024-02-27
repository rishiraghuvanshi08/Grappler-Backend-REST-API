FROM maven:3.8.2-openjdk-11 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:11-jdk-slim
COPY --from=build /target/Grappler-Enhancement-0.0.1-SNAPSHOT.jar argon.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","argon.jar"]
