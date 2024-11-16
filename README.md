# Hairstyle - Sistema de Agendamento e Gerenciamento para Serviços de Beleza e Bem-Estar

Este projeto é um sistema completo de agendamento e gerenciamento de serviços de beleza e bem-estar, desenvolvido com Java e arquitetura limpa, utilizando Docker para simplificar o deploy.

## Funcionalidades

1. **Cadastro de Estabelecimentos**:
    - Permite o registro de estabelecimentos, incluindo informações como nome, endereço, serviços oferecidos e horários de funcionamento.

2. **Perfil de Profissionais**:
    - Cadastro de profissionais com detalhes como especialidades, horários disponíveis e tarifas.

3. **Agendamento de Serviços**:
    - Visualização de serviços, disponibilidade e agendamentos online.
    - Envio de confirmações e lembretes automáticos.

4. **Avaliações e Comentários**:
    - Avaliação de estabelecimentos e profissionais após o serviço.

5. **Busca e Filtragem Avançada**:
    - Pesquisa por nome, localização, serviços e avaliações com filtros avançados.

6. **Gerenciamento de Agendamentos**:
    - Painel de controle para reagendamentos, cancelamentos e ajustes na agenda.

7. **Integração com Calendários**:
    - Suporte para sincronização com Google Calendar (opcional).

---

## Instruções para Rodar o Projeto

### Pré-requisitos

- **Java 17** ou superior
- **Docker** instalado na máquina

### Passos

1. Clone o repositório:
   ```bash
   git clone https://github.com/MatheusFDS/hairstyle.git
    
2. Navegue até o diretório do projeto:  
   ```bash
    cd hairstyle
      
3. Construa as imagens do Docker:
    ```bash
    docker-compose build
 
4. Inicie o ambiente:
     ```bash
     docker-compose up

###  Fluxo de Uso do Sistema
Para utilizar o sistema, siga a sequência abaixo usando a documentação Swagger disponível em http://localhost:8080/swagger-ui/index.html#:
1. **cria um usuário:**
   - Endpoint: POST /api/usuarios
       ```bash
     {
     "username": "seu-usuario",
     "password": "sua-senha",
     "role": "ROLE_ADMIN"
     }
  
2. **cria um usuário:**
   - Endpoint: POST /api/auth/login
       ```bash
     {
     "username": "seu-usuario",
     "password": "sua-senha"
     }

**Criar um estabelecimento (com o token)**:

    Use o token no header de autenticação (Bearer Token).
    Endpoint: POST /api/estabelecimentos
    Criar um profissional (com o token):

Endpoint: POST /api/profissionais
Criar um serviço (com o token):

Endpoint: POST /api/servicos
Criar um cliente:

Endpoint: POST /api/clientes
Criar disponibilidade para os profissionais (com o token):

Endpoint: POST /api/horarios-disponiveis/profissional/{id}
Criar um agendamento (com o token):

Endpoint: POST /api/agendamentos
Avaliar o estabelecimento ou profissional (com o token):

Endpoint para avaliar profissional: POST /api/avaliacoes/profissional/{agendamentoId}
Endpoint para avaliar estabelecimento: POST /api/avaliacoes/estabelecimento/{agendamentoId}
Realizar buscas avançadas dos estabelecimentos:

Endpoint: GET /api/estabelecimentos/filtros
Parâmetros opcionais: nome, endereco, precoMin, precoMax, servico, avaliacaoMinima