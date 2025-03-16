FROM eclipse-temurin:17
RUN mkdir /monitorbot
COPY target/Monitorbot-d4j-1.0.jar /monitorbot/Monitorbot-d4j.jar
CMD ["java", "-jar", "/monitorbot/Monitorbot-d4j.jar"]
