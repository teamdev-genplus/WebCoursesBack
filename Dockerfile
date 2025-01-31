FROM openjdk:17
VOLUME /tmp
EXPOSE 8080
ARG JAR_FILE=target/WebCoursesBack-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} WebCoursesBack.jar
ENTRYPOINT ["java","-jar","/WebCoursesBack.jar"]

# # Etapa 1: Construcción del JAR
# FROM maven:3.8.6-eclipse-temurin-17 AS builder
# WORKDIR /app
# COPY . .
# RUN mvn clean package -DskipTests

# # Etapa 2: Ejecutar la aplicación
# FROM openjdk:17-jdk-slim
# WORKDIR /app
# COPY --from=builder /app/target/WebCoursesBack-0.0.1-SNAPSHOT.jar app.jar
# EXPOSE 8081
# ENTRYPOINT ["java", "-jar", "app.jar"]
