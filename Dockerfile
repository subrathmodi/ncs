# Step 1: Build the application using Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Step 2: Run the application using a slim Java runtime
FROM eclipse-temurin:17-jre-jammy
COPY --from=build /target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]