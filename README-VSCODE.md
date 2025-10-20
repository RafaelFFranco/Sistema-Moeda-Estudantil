# 🚀 Sistema Moeda Estudantil - Configuração VS Code

## ✅ Problemas Corrigidos

### 1. **Classpath Corrigido**
- ✅ Criado `.vscode/settings.json` com configuração de `sourcePaths`
- ✅ Configurado `java.project.sourcePaths` para `["src/main/java"]`
- ✅ Adicionadas configurações de build automático

### 2. **Maven Otimizado**
- ✅ Plugin Spring Boot configurado com `fork=true`
- ✅ MainClass especificada no plugin
- ✅ Configurações de execução rápida

### 3. **Configuração de Execução**
- ✅ `.vscode/launch.json` com duas configurações:
  - **Run MoedaEstudantilApplication (Fast)**: Execução rápida com JVM otimizada
  - **Debug MoedaEstudantilApplication**: Debug completo
- ✅ Argumentos JVM otimizados: `-noverify -XX:TieredStopAtLevel=1`

### 4. **Properties Otimizadas**
- ✅ `application.properties` configurado para execução rápida
- ✅ Lazy initialization habilitada
- ✅ Configurações de desenvolvimento otimizadas

### 5. **Scripts de Execução**
- ✅ `run-fast.sh` (Linux/Mac)
- ✅ `run-fast.bat` (Windows)
- ✅ Compilação e execução em um comando

### 6. **VS Code Otimizado**
- ✅ `.vscode/tasks.json` com tarefas Maven
- ✅ Configurações de performance
- ✅ Exclusões de arquivos desnecessários

## 🎯 Como Executar

### Opção 1: VS Code (Recomendado)
1. Abra o VS Code no diretório do projeto
2. Pressione `Ctrl+Shift+D` (Run and Debug)
3. Selecione "Run MoedaEstudantilApplication (Fast)"
4. Pressione `F5` ou clique no botão play

### Opção 2: Scripts
**Windows:**
```bash
run-fast.bat
```

**Linux/Mac:**
```bash
./run-fast.sh
```

### Opção 3: Maven Direto
```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-noverify -XX:TieredStopAtLevel=1"
```

## 🔧 Configurações Aplicadas

### JVM Otimizada
- `-noverify`: Desabilita verificação de bytecode
- `-XX:TieredStopAtLevel=1`: Limita compilação JIT para startup mais rápido

### Spring Boot Otimizado
- `spring.main.lazy-initialization=true`: Inicialização preguiçosa
- `spring.jpa.open-in-view=false`: Melhora performance
- `spring.thymeleaf.cache=false`: Desabilita cache em desenvolvimento

### VS Code Otimizado
- Build automático habilitado
- Hot Code Replace ativo
- Exclusões de arquivos para melhor performance
- Tasks Maven configuradas

## 🌐 Acesso à Aplicação

Após executar, acesse:
- **Aplicação**: http://localhost:8080
- **H2 Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Username: `sa`
  - Password: (vazio)

## 🐛 Troubleshooting

### Se ainda aparecer erro de classpath:
1. Feche o VS Code
2. Delete a pasta `.vscode` (se existir)
3. Reabra o VS Code
4. Aguarde a indexação do Java Language Server
5. Execute novamente

### Se a aplicação não iniciar:
1. Verifique se o Java 17 está instalado
2. Execute `mvn clean compile` para verificar dependências
3. Verifique se a porta 8080 está livre

## 📝 Notas Importantes

- A aplicação usa H2 em memória (dados são perdidos ao reiniciar)
- Security está desabilitada para facilitar desenvolvimento
- Logs SQL estão habilitados para debug
- Cache do Thymeleaf está desabilitado para desenvolvimento
