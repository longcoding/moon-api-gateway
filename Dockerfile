FROM openjdk:8-jdk-alpine

MAINTAINER longcoding <longcoding@gmail.com>

RUN /bin/sh -c "apk add --no-cache bash"
ADD build/libs/moon-api-gateway-1.0-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-Dserver.port=8080", "-Dspring.profiles.active=local", "-Dlogging.path=./logs", "-jar", "/app.jar"]
