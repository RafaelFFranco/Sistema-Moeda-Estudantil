#!/bin/bash

echo "🚀 Iniciando execução rápida do Sistema Moeda Estudantil..."

# Limpar e compilar o projeto
echo "📦 Compilando projeto..."
mvn clean package -DskipTests -q

# Verificar se a compilação foi bem-sucedida
if [ $? -eq 0 ]; then
    echo "✅ Compilação concluída com sucesso!"
    
    # Executar a aplicação
    echo "🏃 Executando aplicação..."
    java -noverify -XX:TieredStopAtLevel=1 -jar target/*.jar
else
    echo "❌ Erro na compilação. Verifique os logs acima."
    exit 1
fi
