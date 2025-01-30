FROM openjdk:17

EXPOSE 8085

COPY target/MoneyTransferService-0.0.1-SNAPSHOT.jar project.jar

CMD ["java", "-jar", "project.jar"]