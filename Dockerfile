
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app


COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline


COPY src ./src
RUN mvn -q -DskipTests package


FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar


ENV DB_URL=jdbc:postgresql://postgres:5432/quizdb
ENV DB_USER=quiz
ENV DB_PASSWORD=quiz

ENTRYPOINT ["java","-jar","/app/app.jar"]
