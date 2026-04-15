FROM maven:3.9.14-eclipse-temurin-21 AS build
WORKDIR /app
COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .
COPY src ./src
RUN ./mvnw clean package -DskipTests


FROM eclipse-temurin:21-jre-noble
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]