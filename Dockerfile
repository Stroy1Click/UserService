FROM openjdk:21
LABEL authors="egorm"

WORKDIR /app
ADD maven/Stroy1Click-UserService-0.0.1-SNAPSHOT.jar /app/user.jar
EXPOSE 4040
ENTRYPOINT ["java", "-jar", "user.jar"]