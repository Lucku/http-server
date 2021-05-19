FROM maven:3.8-jdk-11 AS builder
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package

# TODO: copy configuration file
FROM openjdk:11-jre-slim
LABEL maintainer="lucask@mailbox.org"

COPY --from=builder /usr/src/app/target/adobe-http-server-1.0.jar /usr/app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/app/app.jar"]