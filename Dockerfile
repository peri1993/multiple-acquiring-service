#FROM openjdk:8-jdk-alpine
#RUN apk --update add tzdata && \
#    cp /usr/share/zoneinfo/Asia/Jakarta /etc/localtime && \
#    apk del tzdata && \
#   rm -rf /var/cache/apk/*
#RUN apk add --no-cache fontconfig ttf-dejavu

# Use OpenJDK 17 base image
#FROM openjdk:17-jdk-alpine

# Use OpenJDK 21 base image
FROM eclipse-temurin:21-jdk-alpine

# Install fonts (example for Alpine Linux; modify if using a different base image)
RUN apk update && \
    apk add --no-cache \
    fontconfig \
    ttf-dejavu
	
RUN apk add --no-cache msttcorefonts-installer fontconfig
RUN update-ms-fonts

ADD target/multiple-acquiring-service-0.0.1-SNAPSHOT.jar multiple-acquiring-service-0.0.1-SNAPSHOT.jar
EXPOSE 9000
ENTRYPOINT ["java","-jar","multiple-acquiring-service-0.0.1-SNAPSHOT.jar"]