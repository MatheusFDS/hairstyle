# Use uma imagem do JDK como base
FROM openjdk:17-jdk-slim

# Crie o diretório de trabalho
WORKDIR /app

# Copie o arquivo jar gerado pela aplicação para o container
COPY target/hairstylefiap-0.0.1-SNAPSHOT.jar app.jar

# Exponha a porta em que a aplicação estará rodando
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
