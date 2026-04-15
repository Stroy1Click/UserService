FROM eclipse-temurin:21-jre-alpine
LABEL authors="egorm"

WORKDIR /app
COPY target/user-service-0.0.1-SNAPSHOT.jar /app/user.jar
EXPOSE 4040
ENTRYPOINT ["java", "-jar", "user.jar"]