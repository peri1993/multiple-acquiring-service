FROM openjdk:8-jdk-alpine
RUN  apk update && apk upgrade && apk add netcat-openbsd
RUN apk add tzdata
RUN cp /usr/share/zoneinfo/Asia/Jakarta /etc/localtime
RUN mkdir -p /usr/local/transaction-service
# ADD @project.build.finalName@.jar /usr/local/transaction-service/
ADD transaction-service-0.0.1-SNAPSHOT.jar /usr/local/transaction-service/
ADD run.sh run.sh
RUN chmod +x run.sh
CMD ./run.sh
