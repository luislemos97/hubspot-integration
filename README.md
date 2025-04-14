# HubSpot Integration API

Esta aplicação é uma API REST desenvolvida em Java com Spring Boot para integrar com a API do HubSpot utilizando o fluxo OAuth 2.0 (authorization code flow). A aplicação permite:

- Gerar a URL de autorização para iniciar o fluxo OAuth.
- Processar o callback OAuth para trocar o código pelo access token.
- Criar contatos no HubSpot através da API.
- Receber notificações via webhooks para a criação de contatos.

> **Observação:**  
> Para testes, o fluxo OAuth obtém o access token que pode ser configurado manualmente via `application.properties` ou obtido dinamicamente através do fluxo de callback.

## Índice

- [Pré-requisitos](#pré-requisitos)
- [Configuração do HubSpot](#configuração-do-hubspot)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Configuração da Aplicação](#configuração-da-aplicação)
- [Como Executar](#como-executar)
- [Endpoints Disponíveis](#endpoints-disponíveis)
- [Testando a API](#testando-a-api)
- [Possíveis Melhorias](#possíveis-melhorias)

## Pré-requisitos

- **Java 11 ou superior:** Certifique-se de ter o JDK instalado.  
- **Maven:** Gerenciador de dependências e construção do projeto.  
- **Visual Studio Code (ou outra IDE):** Recomenda-se o VS Code com o Extension Pack for Java para facilitar o desenvolvimento.  
- **Conta HubSpot:** Uma conta não-desenvolvedor (CRM) para testar a integração com a API do HubSpot.

## Configuração do HubSpot

1. **Crie um aplicativo no HubSpot:**
   - Acesse [HubSpot Developer](https://developers.hubspot.com/) e crie um novo App.
   - Configure a URL de redirecionamento (exemplo: `http://localhost:8080/api/auth/callback`).
   - Selecione os escopos necessários (por exemplo, `crm.objects.contacts.read`, `crm.objects.contacts.write` e `oauth`).

2. **Obtenha as credenciais:**
   - **Client ID:** Exemplo: `a2bbe529-d6e9-4fd2-9bbf-ba83f1151a86`
   - **Client Secret:** Exemplo: `63ba6af1-149b-4460-99d3-dfe96a1088e6`

## Estrutura do Projeto

```
hubspot-integration/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── meetime/
│       │           └── hubspotintegration/
│       │               ├── HubSpotIntegrationApplication.java      // Classe principal da aplicação
│       │               ├── controller/
│       │               │   └── HubSpotController.java              // Endpoints da API
│       │               └── service/
│       │                   └── HubSpotService.java                 // Lógica de integração com HubSpot
│       └── resources/
│           └── application.properties                              // Configurações da aplicação e credenciais HubSpot
├── README.md                                                       // Documentação e instruções de execução
```
## Configuração da Aplicação

Edite o arquivo `src/main/resources/application.properties` com as informações obtidas no HubSpot:

```properties
# Configurações do HubSpot
hubspot.client_id=a2bbe529-d6e9-4fd2-9bbf-ba83f1151a86
hubspot.client_secret=63ba6af1-149b-4460-99d3-dfe96a1088e6
hubspot.redirect_uri=http://localhost:8080/api/auth/callback
hubspot.scopes=crm.objects.contacts.write crm.objects.contacts.read oauth
# Durante os testes, o token pode ficar vazio para que seja obtido via fluxo OAuth:
hubspot.access_token=
````
## Como Executar

Clone o repositório:

git clone https://github.com/seu-usuario/hubspot-integration.git
cd hubspot-integration
Compile a aplicação:

- **Utilize o Maven para compilar:**

mvn clean package
ou, diretamente, rode:

mvn spring-boot:run
Acesse a aplicação:

A aplicação será iniciada na porta 8080. Você pode verificar acessando:

http://localhost:8080/api/auth/url para gerar a URL de autorização.

## Endpoints Disponíveis

- **Geração de URL de Autorização**

GET /api/auth/url

- **Retorna a URL de autorização para iniciar o fluxo OAuth.**

- **Processamento do Callback OAuth**

GET /api/auth/callback

- **Recebe o código de autorização e troca por um access token.**

- **Criação de Contatos**

POST /api/contacts

- **Cria um contato no HubSpot.**

Exemplo de Payload JSON:

{
  "properties": {
    "firstname": "João",
    "lastname": "Silva",
    "email": "joao.silva@example.com",
    "phone": "123456789"
  }
}

- **Recebimento de Webhook para Criação de Contatos**

POST /api/webhook/contacts

- **Recebe notificações de eventos, por exemplo, "contact.creation".**

## Testando a API
Fluxo OAuth:

Acesse http://localhost:8080/api/auth/url no navegador.

Autorize seu aplicativo na página do HubSpot e observe o redirecionamento para http://localhost:8080/api/auth/callback com o parâmetro code.

O código de callback será trocado pelo access token (verifique os logs do terminal).

- **Criação de Contatos via Postman:**

Configure uma requisição POST para http://localhost:8080/api/contacts.

Selecione o header Content-Type: application/json.

No body, insira os dados do contato conforme o exemplo fornecido.

Envie a requisição e verifique a resposta.
Observação: Caso o access token não esteja definido (se não tiver sido obtido via OAuth), a aplicação retornará um erro. Nesse caso, insira temporariamente o token no application.properties para testar.

- **Testando o Webhook:**

Envie uma requisição POST para http://localhost:8080/api/webhook/contacts utilizando uma ferramenta como Postman.

- **Envie um payload simulado, por exemplo:**

{
  "event": "contact.creation",
  "data": {
    "id": "123",
    "email": "novo.contato@example.com"
  }
}

Verifique se a resposta indica que o webhook foi recebido com sucesso.
