#!/bin/bash
# ============================================================
# NivelSeguro - Subir o ambiente com Docker Compose (DENTRO da VM)
#
# Como executar (na pasta raiz do projeto, dentro da VM):
#   ./scripts/vm/subir-ambiente.sh
# ============================================================
set -e

# Vai para a raiz do projeto (pasta onde esta o docker-compose.yml)
cd "$(dirname "$0")/../.."

if [ ! -f .env ]; then
    echo "Arquivo .env nao encontrado. Criando a partir do .env.example..."
    cp .env.example .env
    echo "IMPORTANTE: troque as senhas com 'nano .env' (recomendado antes da primeira subida)."
fi

echo ""
echo ">> docker compose up -d --build  (o primeiro build pode levar de 5 a 10 minutos)"
docker compose up -d --build

echo ""
echo ">> Aguardando a API responder em http://localhost:8080/actuator/health ..."
for tentativa in $(seq 1 40); do
    if curl -fs http://localhost:8080/actuator/health > /dev/null 2>&1; then
        echo "API no ar!"
        break
    fi
    if [ "$tentativa" -eq 40 ]; then
        echo "A API ainda nao respondeu. Verifique: docker compose logs nivelseguro-api"
        exit 1
    fi
    sleep 5
done

echo ""
docker compose ps

IP_PUBLICO=$(curl -fs --max-time 5 https://api.ipify.org || echo "IP-DA-VM")
echo ""
echo "======================================================"
echo " Ambiente NivelSeguro em execucao"
echo "   API (dentro da VM): http://localhost:8080/"
echo "   API (internet)    : http://$IP_PUBLICO:8080/"
echo "   Health            : http://$IP_PUBLICO:8080/actuator/health"
echo "======================================================"
