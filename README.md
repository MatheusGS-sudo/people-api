# People API

Aplicação desenvolvida para avaliação técnica, composta por:

- **Backend:** Java com Spring Boot
- **Frontend:** Angular
- **Banco de dados:** H2 em memória
- **API externa:** Nationalize API
- **Autenticação:** HTTP Basic para operações protegidas

## Funcionalidades

- Cadastrar pessoas
- Listar pessoas cadastradas
- Buscar uma pessoa pelo ID
- Consultar uma possível nacionalidade pelo nome
- Autenticar um usuário
- Excluir uma pessoa autenticada
- Acessar o banco em memória pelo H2 Console


## Pré-requisitos

Antes de executar a aplicação, instale:

- Java 21
- Node.js e npm
- Git, caso o projeto seja obtido pelo GitHub

O Maven não precisa estar instalado globalmente quando os arquivos `mvnw` e `mvnw.cmd` estiverem presentes.

Também verifique se estas portas estão livres:

- Backend: `8080`
- Frontend: `4200`

## Executar no Windows

Coloque o arquivo `iniciar-windows.bat` na raiz do projeto e execute com dois cliques.

Também é possível executar pelo PowerShell:

```powershell
.\iniciar-windows.bat
```

O script:

1. Verifica se Java e npm estão instalados.
2. Instala as dependências do Angular quando `frontend/node_modules` não existir.
3. Abre o backend em uma janela.
4. Abre o frontend em outra janela.

## Executar no Linux

Primeiro, dê permissão de execução:

```bash
chmod +x iniciar-linux.sh
```

Depois execute:

```bash
./iniciar-linux.sh
```

O backend e o frontend serão executados no mesmo terminal. Para encerrar os dois processos, pressione `Ctrl + C`.

## Executar manualmente

### Backend no Windows

```powershell
.\mvnw.cmd spring-boot:run
```

### Backend no Linux

```bash
./mvnw spring-boot:run
```

Caso o projeto não possua o Maven Wrapper:

```bash
mvn spring-boot:run
```

### Frontend

Abra outro terminal:

```bash
cd frontend
npm install
npm start
```

## Endereços da aplicação

Depois de iniciar os serviços:

- Frontend: `http://localhost:4200`
- Backend: `http://localhost:8080`
- H2 Console: `http://localhost:8080/h2-console`

Para acessar o H2 Console, utilize os dados definidos em:

```text
src/main/resources/application.properties
```

## Endpoints principais

| Método | Endpoint | Descrição | Autenticação |
|---|---|---|---|
| POST | `/registrarName` | Cadastra uma pessoa | Não |
| GET | `/list` | Lista as pessoas | Não |
| GET | `/list/{id}` | Busca uma pessoa pelo ID | Não |
| GET | `/findNacionalityByPerson/{id}` | Consulta a nacionalidade | Não |
| GET | `/auth/check` | Valida as credenciais | Sim |
| DELETE | `/list/{id}` | Exclui uma pessoa | Sim |

## Exemplo de cadastro

```http
POST http://localhost:8080/registrarName
Content-Type: application/json
```

```json
{
  "documento": "12345678901",
  "nome": "Matheus",
  "sobrenome": "Silva",
  "email": "matheus@email.com"
}
```

## Autenticação de demonstração

```text
Usuário: admin
Senha: admin123
```

A exclusão de registros exige autenticação. O frontend solicita o login e envia o cabeçalho HTTP Basic ao backend.


## Banco H2

O banco H2 está configurado em memória. Isso significa que os registros podem ser apagados quando o backend for encerrado ou reiniciado.

## Problemas comuns

### O frontend informa que não conseguiu acessar o backend

Confira:

- Se o Spring Boot está rodando na porta `8080`
- Se o frontend está acessando `http://localhost:8080`
- Se o CORS permite `http://localhost:4200`
- Se não existe outro programa utilizando a porta `8080`

### A porta já está em uso

No Windows:

```powershell
netstat -ano | findstr :8080
netstat -ano | findstr :4200
```

No Linux:

```bash
ss -ltnp | grep 8080
ss -ltnp | grep 4200
```

### `npm` não foi encontrado

```bash
node --version
npm --version
```

### Versão incorreta do Java

```bash
java --version
```

O projeto está configurado para Java 21.

## Encerramento

No Windows, feche as duas janelas abertas pelo arquivo `.bat`.

No Linux, pressione `Ctrl + C` no terminal em que o arquivo `.sh` está sendo executado.
