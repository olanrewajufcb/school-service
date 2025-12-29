
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY build/libs/school-service.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
