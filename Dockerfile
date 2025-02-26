# Etapa 1: Construcción del JAR
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: Construcción del Contenedor Final
FROM openjdk:17
WORKDIR /app
COPY --from=build /app/target/WebCoursesBack-0.0.1-SNAPSHOT.jar WebCoursesBack.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/WebCoursesBack.jar"]
