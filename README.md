# Hairstyle - Sistema de Agendamento e Gerenciamento para Serviços de Beleza e Bem-Estar

Este projeto é um sistema completo de agendamento e gerenciamento de serviços de beleza e bem-estar, desenvolvido com Java e arquitetura limpa, utilizando Docker para simplificar o deploy.

## Funcionalidades 

1. **Cadastro de Estabelecimentos**:
    - Permite o registro de estabelecimentos, incluindo informações como nome, endereço, serviços oferecidos e horários de funcionamento.

2. **Perfil de Profissionais**:
    - Cadastro de profissionais com detalhes como especialidades, horários disponíveis e tarifas.

3. **Seriços oferecidos**:
    - Cadastro de serviços com detalhes e tarifas.
  
4. **Cadastro de clientes**:
    - Cadastro de clientes.

5. **Agendamento de Serviços**:
    - Visualização de serviços, disponibilidade e agendamentos online.
    - Envio de confirmações e lembretes automáticos.

6. **Avaliações e Comentários**:
    - Avaliação de estabelecimentos e profissionais após o serviço.

7. **Busca e Filtragem Avançada**:
    - Pesquisa por nome, localização, serviços e avaliações com filtros avançados.

8. **Gerenciamento de Agendamentos**:
    - Painel de controle para reagendamentos, cancelamentos e ajustes na agenda.

9. **Integração com Calendários**:
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
    docker-compose --build
 
4. Inicie o ambiente:
     ```bash
     docker-compose up

5. Rodar testes (obs: como é a primeira vez que irá rodar o projeto ainda não terá usuário por tanto rode duas vezes o comando a abaixo)
    ```bash
     mvn test
   
6. Caso tenha o banco de dados postgres intalado ou no docker já rodando na porta 5432 você pode usar os seguintes comandos para rodar a aplicação: 
   ```bash
      mvn spring-boot:run
   
7. ou criar o ponto jar e execulta-lo
   ```bash
    mvn clean package -DskipTests

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
  
2. **logar com um usuário:**
   - Endpoint: POST /api/auth/login
       ```bash
     {
     "username": "seu-usuario",
     "password": "sua-senha"
     }

```bash
**Criar um estabelecimento (com o token)**:

    Use o token no header de autenticação (Bearer Token).
    Endpoint: POST /api/estabelecimentos

    {
      "nome": "Salão  2",
      "endereco": "Rua Exemplo, 223",
      "horariosFuncionamento": "Segunda a Sexta, 9h às 18h"
    }

    Criar um profissional (com o token):
            Endpoint: POST /api/profissionais
        Criar um serviço (com o token):
{
              "nome": "Teste Cabeleireiro",
              "especialidade": "Cortes Masculinos",
              "telefone": "99999-9999",
              "tarifa": 120.0,
              "estabelecimento": {
                "id": "d333d20b-35d2-4947-96ad-043696e1f5ff"
              }
        }
            




        Endpoint: POST /api/servicos
        Criar um cliente:

    {
        "nome": "Manicure e Pedicure",
        "descricao": "Serviço completo de unhas",
        "preco": 40.00,
        "duracao": 45,
        "estabelecimento": {
        "id": "d333d20b-35d2-4947-96ad-043696e1f5ff"
      }
    }

        
        Endpoint: POST /api/clientes
        Criar disponibilidade para os profissionais (com o token):

{
    "nome": "José Souza",
    "telefone": "(11) 92345-6788",
    "email": "jose.souza@example.com"
}


        
        Endpoint: POST /api/horarios-disponiveis/profissional/{id}
        Criar um agendamento (com o token):

{
  "diaSemana": "FRIDAY",
  "horaInicio": "09:00",
  "horaFim": "18:00"
}

        
        Endpoint: POST /api/agendamentos
        Avaliar o estabelecimento ou profissional (com o token):

{
    "dataHora": "2024-11-15T17:00:00",
    "cliente": {
        "id": "1e377bb8-0aa6-4427-ad69-375075d4eed1"
    },
    "profissional": {
        "id": "79ae1e09-3899-4e53-aa98-2f788424b393"
    },
    "servico": {
        "id": "52460b17-8988-4375-95ed-48655844f0e9"
    }
}

        
        Endpoint para avaliar profissional: POST /api/avaliacoes/profissional/{agendamentoId}
        Endpoint para avaliar estabelecimento: POST /api/avaliacoes/estabelecimento/{agendamentoId}
        Realizar buscas avançadas dos estabelecimentos:
        
        Endpoint: GET /api/estabelecimentos/filtros
        Parâmetros opcionais: nome, endereco, precoMin, precoMax, servico, avaliacaoMinima
```

