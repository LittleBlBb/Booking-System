FROM openjdk:21-ea
LABEL authors="LittleBlBb"

ARG APP_JAR=*.jar

COPY ${APP_JAR} app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]

EXPOSE 8080