# Dockerfile
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . .
RUN ./mvnw -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENV DB_URL=jdbc:postgresql://db:5432/quiz
ENV DB_USER=quiz
ENV DB_PASS=quizpass
ENTRYPOINT ["java","-jar","/app/app.jar"]
