getPort() {
    echo $1 | cut -d : -f 3 | xargs basename
}

java -Djava.security.egd=file:/dev/./urandom -Dserver.port=$SERVER_PORT   \
     -Dspring.profiles.active=$PROFILE -jar /usr/local/transaction-service/transaction-service-0.0.1-SNAPSHOT.jar
