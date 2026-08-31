# 📄 DocManager

> Plataforma backend modular para gerenciamento de documentos, construída com microsserviços em Java 21 + Spring Boot 3.4.

[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-blue)](LICENSE)

---

## 📋 Índice

- [Sobre](#-sobre)
- [Arquitetura](#-arquitetura)
- [Microsserviços](#-microsserviços)
- [Tecnologias](#-tecnologias)
- [Quick Start](#-quick-start)
- [Endpoints](#-endpoints)
- [Testes](#-testes)
- [Docker](#-docker)
- [Extensibilidade](#-extensibilidade)
- [Estrutura do Projeto](#-estrutura-do-projeto)

---

## 🎯 Sobre

DocManager é um **framework reutilizável** para gerenciamento de documentos. Diferente de um sistema fechado, foi projetado como uma plataforma extensível que permite:

- **Cadastrar, consultar, atualizar e excluir** documentos
- **Armazenar arquivos** com estratégias plugáveis (local, nuvem, banco)
- **Gerenciar metadados** com tags, categorias e propriedades customizadas
- **Controlar versões** com histórico completo de alterações

---

## 🏗 Arquitetura

```
┌─────────────────────────────────────────────────────┐
│                   Cliente / API                      │
└───────────────────────┬─────────────────────────────┘
                        │
         ┌──────────────┼──────────────┐
         │              │              │
         ▼              ▼              ▼
┌─────────────┐ ┌─────────────┐ ┌─────────────┐
│  Document   │ │  Metadata   │ │  Version    │
│  Service    │ │  Service    │ │  Service    │
│  :8081      │ │  :8083      │ │  :8084      │
└──────┬──────┘ └──────┬──────┘ └──────┬──────┘
       │               │              │
       │  ┌────────────┤              │
       │  │            │              │
       ▼  ▼            ▼              ▼
┌─────────────┐   ┌─────────┐   ┌─────────┐
│  Storage    │   │ Postgres│   │ Postgres│
│  Service    │   │ metadata│   │ version │
│  :8082      │   └─────────┘   └─────────┘
└──────┬──────┘
       │
       ▼
  ┌─────────┐
  │ Postgres│
  │ document│
  └─────────┘
```

### Padrões Aplicados

| Padrão | Uso |
|--------|-----|
| **Strategy** | Armazenamento plugável (LocalStorage → S3 → DB) |
| **Factory** | Auto-descoberta de estratégias via Spring DI |
| **DTO** | Separação entre camada de transporte e persistência |
| **Soft Delete** | Exclusão lógica com flag `deleted` |
| **RFC 7807** | Tratamento de erros padronizado com `ProblemDetail` |

---

## 🔧 Microsserviços

### Document Service (`:8081`)
Orquestrador principal. Gerencia o ciclo de vida dos documentos.

### Storage Service (`:8082`)
Armazena e recupera arquivos físicos. Usa Strategy Pattern para permitir múltiplas implementações de armazenamento.

### Metadata Service (`:8083`)
Gerencia metadados dos documentos: tags, categorias e propriedades customizadas.

### Version Service (`:8084`)
Controla versionamento sequencial e mantém histórico completo de alterações.

### Common Module
Módulo compartilhado com DTOs, exceções, validações e utilitários.

---

## 🛠 Tecnologias

| Tecnologia | Versão | Uso |
|-----------|--------|-----|
| Java | 21 | Linguagem principal |
| Spring Boot | 3.4.1 | Framework web |
| Spring Data JPA | 3.4.x | Persistência |
| H2 Database | - | Banco de desenvolvimento |
| PostgreSQL | 16 | Banco de produção |
| Lombok | - | Redução de boilerplate |
| Springdoc OpenAPI | 2.7.0 | Documentação da API |
| JUnit 5 | - | Testes unitários |
| Mockito | - | Mocks para testes |
| Docker | - | Containerização |
| Maven | - | Build e gerenciamento |

---

## 🚀 Quick Start

### Pré-requisitos

- **Java 21+**
- **Maven 3.9+**

### Executar em modo de desenvolvimento

```bash
# Compilar todos os módulos
mvn clean compile

# Rodar todos os testes
mvn clean test

# Subir o Document Service (porta 8081)
cd docmanager-document-service
mvn spring-boot:run

# Em outro terminal, subir o Storage Service (porta 8082)
cd docmanager-storage-service
mvn spring-boot:run

# Subir Metadata Service (porta 8083)
cd docmanager-metadata-service
mvn spring-boot:run

# Subir Version Service (porta 8084)
cd docmanager-version-service
mvn spring-boot:run
```

### Acessar

| Recurso | URL |
|---------|-----|
| Swagger UI (Document) | http://localhost:8081/swagger-ui.html |
| Swagger UI (Storage) | http://localhost:8082/swagger-ui.html |
| Swagger UI (Metadata) | http://localhost:8083/swagger-ui.html |
| Swagger UI (Version) | http://localhost:8084/swagger-ui.html |
| H2 Console | http://localhost:8081/h2-console |
| Actuator Health | http://localhost:8081/actuator/health |

---

## 📡 Endpoints

### Document Service

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `POST` | `/api/v1/documents` | Criar documento (multipart) |
| `GET` | `/api/v1/documents` | Listar (paginado) |
| `GET` | `/api/v1/documents/{id}` | Buscar por ID |
| `GET` | `/api/v1/documents/search` | Buscar por nome/tipo |
| `PUT` | `/api/v1/documents/{id}` | Atualizar |
| `DELETE` | `/api/v1/documents/{id}` | Excluir (soft delete) |
| `GET` | `/api/v1/documents/{id}/download` | Download |

### Storage Service

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `POST` | `/api/v1/storage/upload` | Upload de arquivo |
| `GET` | `/api/v1/storage/{key}` | Download |
| `DELETE` | `/api/v1/storage/{key}` | Remover |
| `GET` | `/api/v1/storage/{key}/exists` | Verificar existência |

### Metadata Service

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `POST` | `/api/v1/metadata` | Criar metadados |
| `GET` | `/api/v1/metadata/document/{docId}` | Por documento |
| `GET` | `/api/v1/metadata/{id}` | Por ID |
| `PUT` | `/api/v1/metadata/{id}` | Atualizar |
| `DELETE` | `/api/v1/metadata/{id}` | Excluir |
| `GET` | `/api/v1/metadata/search` | Buscar por tag/categoria |

### Version Service

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `POST` | `/api/v1/versions` | Criar versão |
| `GET` | `/api/v1/versions/{id}` | Por ID |
| `GET` | `/api/v1/versions/document/{docId}` | Listar versões |
| `GET` | `/api/v1/versions/document/{docId}/latest` | Última versão |
| `GET` | `/api/v1/versions/document/{docId}/history` | Histórico |
| `POST` | `/api/v1/versions/{id}/restore` | Restaurar versão |

---

## 🧪 Testes

```bash
# Rodar todos os testes
mvn clean test

# Rodar testes de um módulo específico
mvn test -pl docmanager-document-service

# Rodar com relatório de cobertura
mvn test jacoco:report
```

### Cobertura de Testes

| Módulo | Unitários | Integração |
|--------|-----------|------------|
| Common | FileUtils, SlugUtils | — |
| Document | Service, Controller | CRUD completo |
| Storage | LocalStrategy, Service | — |
| Metadata | Service | CRUD + busca |
| Version | Service | Versionamento + restauração |

---

## 🐳 Docker

### Com Docker Compose

```bash
# Subir todos os serviços
docker-compose up -d

# Verificar status
docker-compose ps

# Ver logs
docker-compose logs -f document-service

# Parar tudo
docker-compose down

# Parar e remover volumes
docker-compose down -v
```

### Build individual

```bash
# Build de um serviço específico
docker-compose build document-service

# Subir apenas um serviço com dependências
docker-compose up document-service
```

---

## 🔌 Extensibilidade

### Adicionar nova estratégia de armazenamento

1. Crie uma classe que implementa `StorageStrategy`:

```java
@Component
public class S3StorageStrategy implements StorageStrategy {

    @Override
    public StorageType getType() {
        return StorageType.S3;
    }

    @Override
    public String store(String key, byte[] data, String contentType) {
        // Implementação S3
    }

    @Override
    public byte[] retrieve(String key) {
        // Implementação S3
    }

    @Override
    public void delete(String key) {
        // Implementação S3
    }

    @Override
    public boolean exists(String key) {
        // Implementação S3
    }
}
```

2. Adicione o valor ao enum `StorageType`
3. Configure `docmanager.storage.type=S3` no `application.yml`
4. O `StorageStrategyFactory` detecta automaticamente via Spring DI ✨

---

## 📁 Estrutura do Projeto

```
projeto/
├── pom.xml                            # Parent POM
├── docker-compose.yml                 # Orquestração Docker
├── README.md                          # Este arquivo
├── .dockerignore
├── docker/
│   └── init-databases.sh              # Inicialização PostgreSQL
├── docmanager-common/                 # Módulo compartilhado
│   ├── pom.xml
│   └── src/
│       ├── main/java/.../common/
│       │   ├── dto/                   # 11 DTOs
│       │   ├── exception/             # 7 exceções + handler
│       │   ├── model/                 # Enums
│       │   └── util/                  # FileUtils, SlugUtils
│       └── test/
├── docmanager-document-service/       # Serviço de Documentos
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/
├── docmanager-storage-service/        # Serviço de Armazenamento
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/
├── docmanager-metadata-service/       # Serviço de Metadados
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/
└── docmanager-version-service/        # Serviço de Versionamento
    ├── pom.xml
    ├── Dockerfile
    └── src/
```

---

## 📝 Licença

Este projeto é licenciado sob a [MIT License](LICENSE).
