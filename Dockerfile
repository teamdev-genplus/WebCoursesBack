# Etapa 1: Construcción del JAR
FROM maven:3.8.6-openjdk-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: Imagen final ligera para ejecución
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/target/WebCoursesBack-0.0.1-SNAPSHOT.jar WebCoursesBack.jar
VOLUME /tmp
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/WebCoursesBack.jar"]
