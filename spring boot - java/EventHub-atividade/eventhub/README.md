# EventHub — ide e listai todos os eventos

Projeto da atividade **A palavra do Java**. A criação de eventos já funciona.
Sua missão é implementar **GET /eventos**, passando por Controller, Service e Repository.

## O que vem pronto

- Java **25**, Spring Boot **4.1.1** (última versão estável confirmada em 20/09/2026).
- Maven Wrapper: não é necessário instalar Maven.
- POST /eventos, persistência com Spring Data JPA e banco H2 em memória.
- Validação da entrada e resposta de erro amigável com status 400.
- Testes da criação e validação. Nenhuma implementação da listagem.

Fontes de versão: https://start.spring.io/ e https://spring.io/projects/spring-boot/ .
O Boot 4.2 em milestone/SNAPSHOT é uma prévia, não foi usado.

## Antes da aula

Instale um **JDK 25** (não apenas JRE), descompacte o ZIP e abra a pasta que contém `pom.xml`.
Você precisa de internet na primeira execução para baixar Maven e dependências.
Não precisa de PostgreSQL, Docker, Lombok, conta em nuvem nem chave de API.

No IntelliJ IDEA: abra `pom.xml` como projeto, selecione **JDK 25** em Project SDK
(e também no Maven Runner, se necessário) e aguarde a importação Maven.
Execute `EventHubApplication`, ou use o terminal abaixo.

Confira `java -version`. Se JAVA_HOME estiver definido, ele também deve apontar para o JDK 25.

## Executar

Windows PowerShell, dentro da pasta do projeto:

```powershell
.\mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
chmod +x mvnw
./mvnw spring-boot:run
```

A API usa http://localhost:8080. Para encerrar, pressione Ctrl+C.
O endereço `/` não tem página e pode responder 404: isso é normal.
Se a porta estiver ocupada, encerre a outra aplicação ou altere `server.port` em
`src/main/resources/application.properties` e use a nova porta nas requisições.

## Criar eventos antes de listar

Abra `eventos.http` no cliente HTTP do IntelliJ, se disponível, ou copie as requisições
para Postman/Insomnia. Alternativa sem plugin, no PowerShell:

```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:8080/eventos -ContentType 'application/json' -InFile exemplos/evento.json
```

Linux/macOS:

```bash
curl -i -X POST http://localhost:8080/eventos -H 'Content-Type: application/json' --data-binary @exemplos/evento.json
```

A resposta HTTP é **201 Created**, com id gerado e os campos do evento.
Use as duas primeiras requisições de `eventos.http` para cadastrar dois eventos.
Vagas zero ou negativas, campos obrigatórios ausentes e nome/local em branco devem retornar 400.
Não há regra de data futura nesta atividade: a data fixa do exemplo continua utilizável depois da palestra.
A data usa `LocalDateTime`, sem fuso horário, para simplificar.

## Atividade 

1. Localize `EventoController` e `EventoService`. Procure o comentário `ATIVIDADE`.
2. Crie no Controller a rota **GET /eventos**.
3. Faça o Controller chamar uma operação de listagem do Service.
4. No Service, use o Repository para buscar os eventos.
5. Retorne a lista como JSON. Não coloque acesso ao Repository diretamente no Controller.

Pistas, quando precisar: `@GetMapping`, `java.util.List`, `repository.findAll()`.
O Spring Data já oferece a busca básica; não escreva SQL para este exercício.

Contrato esperado:

- HTTP **200 OK** e um array de objetos com `id`, `nome`, `local`, `data` e `vagas`.
- Banco vazio: **200** com `[]`.
- Dois eventos cadastrados: ambos aparecem, com seus ids. Não dependa da ordem.
- POST continua funcionando e entradas inválidas continuam retornando 400.

Antes de implementar GET, **405 Method Not Allowed** é esperado: o caminho já aceita POST,
mas ainda não aceita GET. Isso não é uma falha na instalação.

Para conferir depois de implementar:

```powershell
Invoke-RestMethod http://localhost:8080/eventos
```

Ou `curl -i http://localhost:8080/eventos` no Linux/macOS.
Para verificar `[]`, reinicie a aplicação e chame GET antes de cadastrar qualquer evento.

**Extra:** escreva um teste automatizado para a lista vazia e outro para dois eventos.
A regra de não aceitar vagas zero já está pronta: explique onde ela é verificada.

## Ligação com a apresentação

O projeto usa o mesmo EventHub e os mesmos cinco campos da palestra.
O caminho continua sendo **HTTP → Controller → Service → Repository → banco → resposta**.
Na apresentação, o banco ilustrado é PostgreSQL. Aqui ele foi substituído por **H2**
para que todos executem a atividade sem instalar um servidor de banco.
A interface `EventoRepository extends JpaRepository<Evento, Long>` continua igual.

Os construtores e imports omitidos nos slides estão completos neste projeto.
O tratamento de erros mostrado conceitualmente na palestra está implementado em
`TratadorDeErros`. A validação de vagas zero já está pronta: o trabalho dos alunos é a listagem.

## Onde está cada coisa?

```text
src/main/java/br/com/eventhub/
  EventHubApplication.java         inicia a aplicação
  evento/
    EventoController.java          recebe HTTP (POST pronto; GET é a atividade)
    EventoService.java             executa o caso de uso
    EventoRepository.java          acessa os dados
    Evento.java                    entidade armazenada no H2
    EventoRequest.java             dados de entrada e validações
    RegraDeEventoException.java     erro da regra de vagas
  erro/
    TratadorDeErros.java            transforma erros esperados em HTTP 400
    ErroResponse.java               formato {status, message}
```

Para manter o exemplo pequeno, a entidade também é o JSON de saída. Não há autenticação,
paginação nem outras rotas de CRUD. O código tem finalidade didática e execução local.

## Banco H2

O banco começa **vazio**. Os dados se perdem ao encerrar/reiniciar a aplicação.
JPA cria as tabelas automaticamente; não há script de carga escondido.

Opcional: abra http://localhost:8080/h2-console enquanto a aplicação estiver rodando.

- Driver: `org.h2.Driver`
- JDBC URL: `jdbc:h2:mem:eventhub` (copie exatamente; não use a URL padrão do console)
- Usuário: `sa`
- Senha: deixe vazia

Execute `SELECT * FROM EVENTO;` para conferir os dados. O console é só uma ferramenta local da aula.

## Testes e empacotamento

```powershell
.\mvnw.cmd test
.\mvnw.cmd clean package
java -jar target/eventhub-1.0.0.jar
```

No Linux/macOS, substitua `.\mvnw.cmd` por `./mvnw`.
Os testes existentes verificam o que já vem pronto. Eles não resolvem nem exigem GET.
Não altere os testes de criação para fazer a listagem passar.

“Não salvarás sem validar.” Agora, ide e listai os eventos.
