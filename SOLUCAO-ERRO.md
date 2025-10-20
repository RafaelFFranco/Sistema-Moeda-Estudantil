# 🔧 Solução para "The project 'Sistema-Moeda-Estudantil' is not a valid java project"

## ✅ Correções Aplicadas

### 1. **Arquivos de Projeto Eclipse**
- ✅ Criado `.project` - Define o projeto como Java + Maven
- ✅ Criado `.classpath` - Define o classpath do projeto
- ✅ Configurado para Java 17 e Maven

### 2. **Configuração VS Code Atualizada**
- ✅ Removido `projectName` do `launch.json` (causava conflito)
- ✅ Simplificado `settings.json` para reconhecimento automático
- ✅ Adicionado `extensions.json` com extensões recomendadas

### 3. **Configurações de Launch Corrigidas**
- ✅ 3 configurações disponíveis:
  - **"Run MoedaEstudantilApplication (Fast)"** - Execução rápida
  - **"Debug MoedaEstudantilApplication"** - Debug completo  
  - **"Spring Boot: Run"** - Execução Spring Boot padrão

## 🚀 Como Executar Agora

### Passo a Passo:

1. **Feche o VS Code completamente**

2. **Reabra o VS Code no diretório do projeto**

3. **Aguarde a indexação** (pode levar 1-2 minutos na primeira vez)

4. **Vá para Run and Debug** (`Ctrl+Shift+D`)

5. **Selecione uma das configurações:**
   - `Run MoedaEstudantilApplication (Fast)` (recomendado)
   - `Debug MoedaEstudantilApplication`
   - `Spring Boot: Run`

6. **Pressione F5 ou clique no botão play**

## 🔍 Se Ainda Der Erro

### Opção 1: Reload Window
1. Pressione `Ctrl+Shift+P`
2. Digite "Developer: Reload Window"
3. Pressione Enter
4. Aguarde a reindexação

### Opção 2: Clean Workspace
1. Pressione `Ctrl+Shift+P`
2. Digite "Java: Clean Workspace"
3. Pressione Enter
4. Confirme a limpeza
5. Aguarde a reindexação

### Opção 3: Verificar Extensões
Certifique-se de que tem instalado:
- Extension Pack for Java
- Spring Boot Extension Pack
- Maven for Java

## 🎯 Alternativas de Execução

### Via Terminal (se VS Code não funcionar):
```bash
# Windows
run-fast.bat

# Linux/Mac  
./run-fast.sh
```

### Via Maven Direto:
```bash
mvn spring-boot:run
```

## 📝 Notas Importantes

- O erro era causado pelo `projectName` no `launch.json`
- Os arquivos `.project` e `.classpath` ajudam o VS Code a reconhecer o projeto
- A primeira execução pode ser mais lenta devido à indexação
- Se usar Java 11, altere a versão no `.classpath` de `JavaSE-17` para `JavaSE-11`

## ✅ Verificação Final

Após seguir os passos, você deve ver:
- ✅ Projeto reconhecido como Java no VS Code
- ✅ Configurações de launch funcionando
- ✅ Aplicação executando em http://localhost:8080
