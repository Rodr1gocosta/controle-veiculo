# 🚗 Controle de Veículos
API REST para gerenciamento de veículos com autenticação JWT, cotação do dólar em tempo real, cache com Redis e Circuit Breaker.
---
## 🛠️ Tecnologias
| Tecnologia | Versão |
|---|---|
| Java | 21 |
| Spring Boot | 4.0.5 |
| Spring Security (OAuth2 Resource Server / JWT) | — |
| Spring Data JPA | — |
| Spring Data Redis | — |
| H2 Database | — |
| Resilience4j (Circuit Breaker) | 2.3.0 |
| SpringDoc OpenAPI (Swagger UI) | 3.0.2 |
| Lombok | — |
| JaCoCo | 0.8.12 |
---
## 📋 Pré-requisitos
- **Java 21** instalado
- **Maven 3.x** instalado (ou usar o wrapper `./mvnw`)
- **Docker** e **Docker Compose** instalados (para o Redis)
- **Git** instalado
---
## ⚙️ Configuração
### 1. Clone o repositório
```bash
git clone <https://github.com/Rodr1gocosta/controle-veiculo.git>
cd controle-veiculo
```
### 2. Arquivo de variáveis de ambiente
Crie o arquivo `.env.properties` na **raiz do projeto** (junto ao `pom.xml`):
```properties
# Usuário padrão Spring Security (usado para gerar o token JWT)
SECURITY_USER=user
SECURITY_PASSWORD=user123
# Banco de dados H2 (em memória)
DB_URL=jdbc:h2:mem:controle_veiculo
DB_DRIVER=org.h2.Driver
DB_USERNAME=sa
DB_PASSWORD=
# Console H2
H2_CONSOLE_ENABLED=true
# JPA
JPA_DDL_AUTO=create-drop
JPA_SHOW_SQL=true
```
> **Nota:** O arquivo `.env.properties` é carregado automaticamente pelo Spring (`spring.config.import`).
---
## 🐳 Subindo o Redis com Docker
O Redis é necessário para o cache de cotação do dólar.
```bash
docker compose up -d
```
Verifique se o container está rodando:
```bash
docker compose ps
```
Para parar:
```bash
docker compose down
```
---
## ▶️ Executando a aplicação
```bash
./mvnw spring-boot:run
```
Ou, se preferir compilar e executar o JAR:
```bash
./mvnw clean package -DskipTests
java -jar target/controle-veiculo-0.0.1-SNAPSHOT.jar
```
A aplicação estará disponível em: **http://localhost:8080**
---
## 👤 Usuários padrão (em memória)
| Usuário | Senha | Role |
|---|---|---|
| `user` | `user123` | USER |
| `admin` | `admin123` | ADMIN |
---
## 🔐 Autenticação
A API utiliza **JWT Bearer Token**. Para obter o token:
### `POST /auth/login`
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```
**Resposta:**
```json
{
  "token": "eyJhbGciOiJSUzI1NiJ9..."
}
```
Use o token nas requisições seguintes:
```bash
curl -H "Authorization: Bearer <token>" http://localhost:8080/veiculos
```
---
## 📡 Endpoints da API
### Veículos — `/veiculos`
| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/veiculos` | Lista todos os veículos (paginado) |
| `GET` | `/veiculos/{id}` | Busca veículo por ID |
| `GET` | `/veiculos/especificacao` | Filtra por `marca`, `ano` e/ou `cor` |
| `GET` | `/veiculos/preco` | Filtra por faixa de preço (`minPreco`, `maxPreco`) |
| `POST` | `/veiculos` | Cria um novo veículo |
| `PUT` | `/veiculos/{id}` | Atualiza completamente um veículo |
| `PATCH` | `/veiculos/{id}` | Atualiza parcialmente um veículo |
| `DELETE` | `/veiculos/{id}` | Remove um veículo |
| `GET` | `/veiculos/relatorios/por-marca` | Relatório agrupado por marca |
### Auth — `/auth`
| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/auth/login` | Autentica e retorna o JWT |
---
## 📦 Exemplos de payload
### Criar veículo (`POST /veiculos`)
```json
{
  "marca": "Toyota",
  "ano": 2024,
  "cor": "Branco",
  "preco": 120000.00,
  "placa": "XYZ-9999",
  "ativo": true
}
```
### Atualizar parcialmente (`PATCH /veiculos/{id}`)
```json
{
  "cor": "Prata",
  "preco": 115000.00
}
```
---
## 🗄️ Console H2 (banco em memória)
Acesse o console do banco de dados H2 em:
**http://localhost:8080/h2-console**
| Campo | Valor |
|---|---|
| JDBC URL | `jdbc:h2:mem:controle_veiculo` |
| User Name | `sa` |
| Password | *(vazio)* |
> A aplicação carrega **10 veículos de teste** automaticamente na inicialização.
---
## 📖 Documentação Swagger UI
Acesse a documentação interativa da API em:
**http://localhost:8080/swagger-ui.html**
---
## 💱 Cotação do Dólar (Circuit Breaker + Cache Redis)
Ao buscar veículos, o sistema pode retornar o preço convertido em USD. A cotação é obtida de duas fontes externas com **Circuit Breaker** (Resilience4j):
1. **Primária:** [AwesomeAPI](https://economia.awesomeapi.com.br/json/last/USD-BRL)
2. **Fallback:** [Frankfurter](https://api.frankfurter.dev/v1/latest?from=USD&to=BRL)
O resultado é **cacheado no Redis** para evitar chamadas desnecessárias.
---
## 🧪 Executando os Testes
### Todos os testes
```bash
./mvnw test
```
### Testes com relatório de cobertura (JaCoCo)
```bash
./mvnw clean verify
```
O relatório HTML de cobertura é gerado em:
```
target/site/jacoco/index.html
```
Abra no navegador para visualizar a cobertura de código.
### Tipos de testes presentes
| Tipo | Localização |
|---|---|
| Testes de domínio | `src/test/.../domain/` |
| Testes de DTOs | `src/test/.../dto/` |
| Testes de exceções | `src/test/.../exception/` |
| Testes de serviços | `src/test/.../service/` |
| Testes de controllers | `src/test/.../controller/` |
| Testes de integração | `src/test/.../integration/` |
### Executar uma classe de teste específica
```bash
./mvnw test -Dtest=VeiculoServiceTest
```
### Executar um método de teste específico
```bash
./mvnw test -Dtest=VeiculoServiceTest#deveCriarVeiculoComSucesso
```
---
## 📁 Estrutura do Projeto
```
src/
├── main/
│   ├── java/com/rodr1gocosta/controle_veiculo/
│   │   ├── config/         # Configurações (Security, JWT, Redis, OpenAPI, DataMocker)
│   │   ├── controller/     # Controllers REST (VeiculoController, AuthController)
│   │   ├── domain/         # Entidades JPA (Veiculo)
│   │   ├── dto/            # DTOs de request/response
│   │   ├── exception/      # Exceções customizadas e GlobalExceptionHandler
│   │   ├── repository/     # Repositórios Spring Data JPA
│   │   └── service/        # Serviços de negócio (VeiculoService, CambioService)
│   └── resources/
│       └── application.yaml
└── test/
    └── java/com/rodr1gocosta/controle_veiculo/
        ├── controller/     # Testes de controllers
        ├── domain/         # Testes de entidades
        ├── dto/            # Testes de DTOs
        ├── exception/      # Testes de exceções
        ├── integration/    # Testes de integração (fluxo completo)
        └── service/        # Testes de serviços
```
---
## 🔧 Variáveis de ambiente — referência completa
| Variável | Descrição | Exemplo |
|---|---|---|
| `SECURITY_USER` | Usuário do Spring Security | `user` |
| `SECURITY_PASSWORD` | Senha do Spring Security | `user123` |
| `DB_URL` | URL JDBC do banco de dados | `jdbc:h2:mem:controle_veiculo` |
| `DB_DRIVER` | Driver JDBC | `org.h2.Driver` |
| `DB_USERNAME` | Usuário do banco | `sa` |
| `DB_PASSWORD` | Senha do banco | *(vazio)* |
| `H2_CONSOLE_ENABLED` | Habilita console H2 | `true` |
| `JPA_DDL_AUTO` | Estratégia DDL do Hibernate | `create-drop` |
| `JPA_SHOW_SQL` | Exibe SQL no log | `true` |
