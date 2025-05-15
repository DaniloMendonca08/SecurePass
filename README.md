# :computer: SecurePass

API RESTful para CRUD de usuários e autenticação, desenvolvida utilizando Java e Spring Boot, seguindo boas práticas de desenvolvimento.

## 🚀 Tecnologias Utilizadas
- Java 17
- Spring Boot
- PostgreSQL
- Docker / Docker Compose
- Swagger (OpenAPI)
- JWT (Autenticação)
- Testes unitários utilizando JUnit e Mockito

## 📦 Instalação e Execução
Requisitos:
- Possuir o Docker e Docker Compose (incluso no Docker Desktop) instalados

## 🔁 Clonar o repositório
```bash
git clone https://github.com/DaniloMendonca08/SecurePass.git
cd SecurePass
```
## Executar a aplicação
```bash
docker-compose up
```

## 📑 Endpoints
Após iniciar a aplicação, você pode conferir com mais detalhes e testar os endpoints com exemplos no link:
```bash
http://localhost:8080/swagger-ui.html
```


| 🔧 Método | 🛣️ Rota              | 📄 Descrição              |
|--------|-------------------|------------------------|
| POST    | `/login`         | Realiza login do usuário |
| PUT   | `/user/update`     | Atualiza dados do usuário |
| POST    | `/user`          | Cria um novo usuário |
| GET    | `/user/info`      | Busca informações do usuário autenticado |
| DELETE | `/user/{id}`      | Exclui um usuário existente |

## 🔐 Autenticação
Após o login (`/login`), será retornado um token JWT.  
Esse token deve ser incluído no cabeçalho das seguintes requisições:
- Atualizar dados (`/user/update`)
- Buscar informações (`/user/info`)
- Excluir um usuário (`/user/{id}`)  

Deverá ser utilizado dessa forma:
```http
Authorization: Bearer <seu_token>
```

## 📫 Contato
Se tiver dúvidas, sugestões ou quiser entrar em contato:

Nome: Danilo Araujo Mendonça

Email: danilomendonca08@gmail.com

LinkedIn: www.linkedin.com/in/danilomendonca08
