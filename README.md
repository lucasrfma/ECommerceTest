# E-commerce Test Project

## Requisitos

*   Java 21
*   Maven ou IntelliJ/outra IDE que cuide disso

## Build e Execução

### Intellij
Apenas abrir o projeto e iniciar os 5 serviços:

  1. Eureka Service
  2. Account Service
  3. Product Service
  4. Sales Service
  5. Gateway Service

Essa ordem não é obrigatória, mas prefiro iniciar o Gateway service após os outros.

Dependendo de ordem/demora entre as inicializações,
pode ser que tenha que esperar um tempo para que o Gateway service conheça todas as rotas.

### Linha de comando Maven

Abra o terminal na pasta do projeto/shared-utils e execute o seguinte comando:
```
mvnw clean install
```

A seguir, há duas opções:
 - abra uma aba/janela de terminal nas pastas de cada serviço e execute o seguinte comando:
   * ps: assim como na opção do IntelliJ, recomendo seguir a ordem mencionada, apesar de não obrigatório:
```
mvn clean spring-boot:run
```
 - Ou, com o powershell na pasta do projeto, rode o script run-services.ps1
```
./run-services.ps1
```
Esse script vai abrir 5 janelas de powershell, cada uma em uma pasta de serviço, e executa o comando anterior.
Ele espera 5 segundos entre cada passo desses.

## Testando os endpoints
- Recomendo usar Insomnia ou Postman para os testes.
- Para usar endpoints marcados com " - AUTH", precisará de um token de acesso.
  - Para gerar um token, use o endpoint de login do Account Service.
  - Uma vez gerado o token, coloque na header do seu request: "Authorization: Bearer <token>"
  - Esse processo é automatizável com o Insomnia/Postman.
 
### Guia de automatização do token

- Criar endpoint de login

<img width="2127" height="630" alt="image" src="https://github.com/user-attachments/assets/82959163-5be2-451d-a766-f488d80cd0e3" />

- Editar environment
  
<img width="325" height="300" alt="image" src="https://github.com/user-attachments/assets/4e90ce0f-ec39-4015-a293-90da1cdce981" />

- Criar uma variável 

<img width="3312" height="570" alt="image" src="https://github.com/user-attachments/assets/0b0bb7bb-1149-4c48-aa51-568e349cec01" />

- Editá-la para preencher automaticamente com o resultado do pedido de login

<img width="1821" height="931" alt="image" src="https://github.com/user-attachments/assets/46c3dc61-40d9-423e-a109-bc7efdf3cdbb" />

- Nos Endpoints que requerem autenticação, na aba Auth escolher a opção Bearer Token e setar a variável no token -> {{access_token}}

<img width="1552" height="572" alt="image" src="https://github.com/user-attachments/assets/3ac79cc6-831f-4bbe-bb30-17a9d197c403" />

### Account Service
- Para acessar o swagger:
- http://localhost:8081/swagger-ui/index.html#/
#### Register Account
- Registra uma conta nova.
- Não pode ser um email repetido.
- Há alguns parâmetros a se seguirem, ex: email tem que ter @, senha entre 8 e 30 caracteres.
```
POST: http://localhost:8080/api/accounts/register
{
    "email": "user.name@esample.com",
    "password": "1364912WaS17",
    "address": "Rua das Flores, 123"
}
```

#### Login Account
- Loga em uma conta existente.
```
POST: http://localhost:8080/api/accounts/login
{
    "email": "user.name@esample.com",
    "password": "1364912WaS17",
}

Retorna um token de acesso.

{
	"access_token": "eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOiIxIiwic3ViIjoidXNlci5uYW1lMkBlc3hhbXBsZS5jb20iLCJpYXQiOjE3NjE2OTA2NjEsImV4cCI6MTc2MTY5MTI2MX0.__PhmfCEsw2O5r0w8hXyNxeqY8ExwUHfab0gqG8BG7_53R-eX1YZwAErmUS3akQoW1WjCR4SjV0oRRwqVdIcMg"
}
```

### Product Service
- Para acessar o swagger:
- http://localhost:8082/swagger-ui/index.html#/

#### Register Product - AUTH
- Registra/atualiza um produto.
- Caso não já haja um produto com essa descrição, é criado um novo.
- Caso haja, é atualizado.
- Portando, único campo obrigatório é description.
- Os campos category e price são substituídos.
- O campo quantity é aditivo.
  - Exemplo: se existe um produto com 500 de estoque, se:
    - vc envia uma ordem com -450, o produto vai ter 50 de estoque.
    - se a ordem for de 450, o produto vai ter 950 de estoque.
    - O estoque não pode se tornar negativo.
```
Exemplos com estoque já existente:

POST: localhost:8080/api/products
{
    "description": "novo prod3",
    "quantity": 2000,
    "category": "beleza",
    "price": 125.99
}

Resposta:

{
    "type": "success",
    "data": {
        "id": 3,
        "description": "novo prod3",
        "category": "beleza",
        "price": 125.99,
        "quantity": 4000
    }
}

POST: localhost:8080/api/products
{
    "description": "novo prod3",
    "quantity": -5000,
    "category": "beleza",
    "price": 125.99
}

Resposta:
{
    "type": "val_failure",
    "message": "stock: O estoque deve ser maior ou igual a zero"
}
```

#### Get All Products
- Lista todos os produtos registrados:
```
GET localhost:8080/api/products

resposta:
[
    {
        "id": 1,
        "description": "novo prod",
        "category": "beleza",
        "price": 125.99,
        "quantity": 1000
    },
    {
        "id": 2,
        "description": "novo prod2",
        "category": "beleza",
        "price": 125.99,
        "quantity": 1000
    }
]
```

#### Get Product By Id - AUTH
- Retorna informações de um produto específico
```
GET localhost:8080/api/products/1
resposta:
{
    "type": "success",
    "data": {
        "id": 1,
        "description": "novo prod",
        "category": "beleza",
        "price": 125.99,
        "quantity": 1000
    }
}
```

### Sales Service
- Para acessar o swagger:
- http://localhost:8083/swagger-ui/index.html#/

#### Register Sale - AUTH
- Cria uma nova venda.
- productId deve ser de um produto existente
- quantity deve ser maior ou igual a 1
- quantity maior que o estoque não será aceita
```
POST localhost:8080/api/sales
{
    "productId": 1,
    "quantity": 1
}

Resposta:

{
    "type": "success",
    "data": {
        "id": 1,
        "productId": 1,
        "productDescription": "novo prod",
        "quantity": 1,
        "price": 125.99,
        "accountEmail": "user.name2@esxample.com",
        "saleDate": "2025-10-28T19:54:42.7308037"
    }
}

POST localhost:8080/api/sales
{
    "productId": 2,
    "quantity": 5000
}

Resposta:
{
    "type": "val_failure",
    "message": "Estoque insuficiente."
}
```
#### Get All Sales - AUTH
- Pega todas as vendas (feitas pelo usuário logado (token))
```
GET localhost:8080/api/sales

Resposta:
[
    {
        "id": 1,
        "productId": 1,
        "productDescription": "novo prod",
        "quantity": 1,
        "price": 125.99,
        "accountEmail": "user.name2@esxample.com",
        "saleDate": "2025-10-28T19:54:42.730804"
    },
    {
        "id": 2,
        "productId": 2,
        "productDescription": "novo prod2",
        "quantity": 50,
        "price": 125.99,
        "accountEmail": "user.name2@esxample.com",
        "saleDate": "2025-10-28T19:55:24.027195"
    }
]
```



