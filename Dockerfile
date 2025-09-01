# Use Maven + JDK image to build
FROM maven:3.8.7-openjdk-18 AS build

WORKDIR /app

# Copy the project
COPY pom.xml .
COPY src ./src

# Build the jar (skip tests)
RUN mvn clean package -DskipTests

# Use JDK slim image for running the jar
FROM openjdk:17-slim
WORKDIR /app

# Copy the built jar from previous stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]