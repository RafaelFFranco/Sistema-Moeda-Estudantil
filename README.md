# Moeda Estudantil - Release 1 (Sprint 2)

Sistema de moeda virtual para estudantes desenvolvido com Spring Boot.

## Tecnologias

- Java 17
- Spring Boot 3.1.6
- Spring Data JPA
- Thymeleaf
- H2 Database
- Maven

## Como rodar

1. Clone o repositório
2. Execute o comando:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```
3. Acesse: http://localhost:8080
4. H2 Console: http://localhost:8080/h2-console
   - JDBC URL: `jdbc:h2:mem:moedadb`
   - User: `sa`
   - Password: (vazio)

## O que foi implementado na Sprint 2

- ✅ Modelo ER (documentado)
- ✅ Estratégia de persistência: Spring Data JPA (ORM)
- ✅ CRUD de Aluno (front + back)
- ✅ CRUD de Empresa Parceira (front + back)
- ✅ Dados iniciais (Instituições)

## Modelo Entidade-Relacionamento

```mermaid
erDiagram
    ALUNO {
        int id PK
        string nome
        string email
        string cpf
        string rg
        string endereco
        string curso
        string login
        string senha
        int saldoMoedas
        int instituicao_id FK
    }
    INSTITUICAO {
        int id PK
        string nome
    }
    EMPRESA_PARCEIRA {
        int id PK
        string nome
        string email
        string login
        string senha
    }

    INSTITUICAO ||--o{ ALUNO : "possui"
    INSTITUICAO ||--o{ PROFESSOR : "possui"   %% professor será adicionado na sprint 3
    ALUNO }o--|| EMPRESA_PARCEIRA : "interage?" %% placeholder para futuras relações (resgate)
```

## Estrutura do Projeto

```
moeda-estudantil/
├─ pom.xml
├─ README.md
├─ src/main/java/br/edu/moedaestudantil/
│  ├─ MoedaEstudantilApplication.java
│  ├─ config/
│  │  └─ WebConfig.java
│  ├─ model/
│  │  ├─ Aluno.java
│  │  ├─ Instituicao.java
│  │  └─ EmpresaParceira.java
│  ├─ repository/
│  │  ├─ AlunoRepository.java
│  │  ├─ InstituicaoRepository.java
│  │  └─ EmpresaRepository.java
│  ├─ service/
│  │  ├─ AlunoService.java
│  │  └─ EmpresaService.java
│  ├─ controller/
│  │  ├─ AlunoController.java
│  │  ├─ EmpresaController.java
│  │  └─ HomeController.java
│  └─ dto/
│     ├─ AlunoForm.java
│     └─ EmpresaForm.java
├─ src/main/resources/
│  ├─ application.properties
│  ├─ data.sql
│  └─ templates/
│     ├─ index.html
│     ├─ aluno/
│     │  ├─ list.html
│     │  ├─ form.html
│     │  └─ view.html
│     └─ empresa/
│        ├─ list.html
│        ├─ form.html
│        └─ view.html
└─ src/main/resources/static/
   └─ css/
      └─ styles.css
```

## Funcionalidades

### Alunos
- Listar todos os alunos
- Criar novo aluno
- Visualizar detalhes do aluno
- Editar aluno existente
- Excluir aluno
- Associação com instituição

### Empresas Parceiras
- Listar todas as empresas
- Criar nova empresa
- Visualizar detalhes da empresa
- Editar empresa existente
- Excluir empresa

## Próximos passos (Sprint 3)

- [ ] Implementar Professor, Transacao e Vantagem
- [ ] Autenticação (login por tipo de usuário)
- [ ] Envio de e-mail ao resgatar vantagens
- [ ] Sistema de transações entre alunos e empresas
- [ ] Dashboard com estatísticas

## Dados Iniciais

O sistema já vem com dados de exemplo:
- 2 Instituições (Universidade X, Faculdade Y)
- 2 Empresas Parceiras (Cantina Universitária, Livraria Campus)
- 1 Aluno exemplo (João da Silva)

## Desenvolvimento

Para desenvolvimento, o cache do Thymeleaf está desabilitado para facilitar a visualização das mudanças em tempo real.

A segurança está desabilitada nesta sprint para facilitar o desenvolvimento e testes.