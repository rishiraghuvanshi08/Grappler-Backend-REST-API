# Stage 1: Build stage
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
# Copying only the pom.xml first allows for better caching of dependencies
RUN mvn dependency:go-offline

# Copying the rest of the source code
COPY . .
# Building the application
RUN mvn clean package -DskipTests

# Stage 2: Production stage
FROM openjdk:17.0.1-jdk-slim
WORKDIR /app
# Copying the built JAR from the build stage
COPY --from=build /app/target/Grappler-Enhancement-0.0.1-SNAPSHOT.jar ./Grappler-Enhancement.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","Grappler-Enhancement.jar"]
