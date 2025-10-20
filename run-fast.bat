@echo off
echo 🚀 Iniciando execução rápida do Sistema Moeda Estudantil...

REM Limpar e compilar o projeto
echo 📦 Compilando projeto...
mvn clean package -DskipTests -q

REM Verificar se a compilação foi bem-sucedida
if %errorlevel% equ 0 (
    echo ✅ Compilação concluída com sucesso!
    
    REM Executar a aplicação
    echo 🏃 Executando aplicação...
    java -noverify -XX:TieredStopAtLevel=1 -jar target\*.jar
) else (
    echo ❌ Erro na compilação. Verifique os logs acima.
    exit /b 1
)

pause
