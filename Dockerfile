FROM alpine/java:21
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /app/controle-veiculo.jar
ENTRYPOINT ["java", "-jar", "/app/controle-veiculo.jar"]
EXPOSE 8080
