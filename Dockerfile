# Usando uma imagem base do JDK para compilar e rodar o projeto
FROM openjdk:17-jdk-slim

# Defina o diretório de trabalho
WORKDIR /app

# Copie o arquivo .jar do seu projeto (garanta que ele esteja construído previamente)
COPY target/hairstylefiap-0.0.1-SNAPSHOT.jar app.jar

# Copie as credenciais do Google para o diretório especificado
COPY credentials.json /app/credentials.json

# Expõe a porta que a aplicação utiliza
EXPOSE 8080

# Define o comando para rodar a aplicação Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
