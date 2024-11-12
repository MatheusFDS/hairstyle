# HairstyleFIAP

Sistema de agendamento e gerenciamento para serviços de beleza e bem-estar, desenvolvido em Spring Boot com banco de dados PostgreSQL e autenticação JWT.

## Índice
- [Funcionalidades](#funcionalidades)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Pré-requisitos](#pré-requisitos)
- [Configuração do Banco de Dados](#configuração-do-banco-de-dados)
- [Configuração do Projeto](#configuração-do-projeto)
- [Autenticação JWT](#autenticação-jwt)
- [Endpoints da API](#endpoints-da-api)
- [Testes com Postman](#testes-com-postman)
- [Possíveis Problemas e Soluções](#possíveis-problemas-e-soluções)
- [Licença](#licença)

## Funcionalidades

O sistema permite:
- Gerenciamento de **Clientes**, **Estabelecimentos**, **Serviços** e **Profissionais**.
- **Agendamentos** vinculados a clientes, serviços e profissionais.
- Autenticação JWT para garantir segurança nas rotas protegidas.

## Tecnologias Utilizadas
- **Java 17**
- **Spring Boot 3.3.4**
- **PostgreSQL 16**
- **Docker** e **Docker Compose**
- **Maven** para gerenciamento de dependências
- **Lombok** para reduzir código boilerplate
- **Spring Security** com **JWT** para autenticação

## Pré-requisitos

- **Java 17** ou superior.
- **Maven** para gerenciamento de dependências.
- **Docker** para containerização do banco de dados PostgreSQL.

Para verificar se você possui as ferramentas instaladas, execute:
```bash
java -version
mvn -version
docker --version
