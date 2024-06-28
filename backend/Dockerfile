FROM openjdk:8-jdk as builder

RUN mkdir /app

WORKDIR /app

USER root

COPY . .

RUN chmod +x ./gradlew

EXPOSE 8080
CMD ["./gradlew", "bootrun","-Dspring.profiles.active=prod"]
