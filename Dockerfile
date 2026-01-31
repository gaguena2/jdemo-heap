FROM maven:3.8.6-eclipse-temurin-17 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY --from=build /app/target/jdemo-heap.jar ./jdemo-heap.jar

ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=60 -XX:+ExitOnOutOfMemoryError -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/app/dumps"


RUN mkdir -p /app/dumps

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar jdemo-heap.jar"]