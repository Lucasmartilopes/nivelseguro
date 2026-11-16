#!/bin/bash
# ============================================================
# NivelSeguro - Teste completo do CRUD nas 3 tabelas relacionadas
#
# Uso:
#   ./scripts/vm/testar-crud.sh                         (dentro da VM)
#   ./scripts/vm/testar-crud.sh http://IP-DA-VM:8080    (do seu computador)
#
# Fluxo: CREATE -> READ -> UPDATE -> SELECT no banco -> DELETE -> SELECT
# Quando executado na VM, tambem mostra o SELECT direto no MySQL.
# ============================================================
BASE_URL="${1:-http://localhost:8080}"
CODIGO_SENSOR="NS-TESTE-$(date +%H%M%S)"

titulo() {
    echo ""
    echo "------------------------------------------------------"
    echo " $1"
    echo "------------------------------------------------------"
}

# Faz a requisicao, imprime o corpo + status HTTP e guarda o corpo em $RESPOSTA
requisicao() {
    local metodo="$1" url="$2" corpo="$3"
    echo "> $metodo $url"
    if [ -n "$corpo" ]; then
        RESPOSTA=$(curl -s -w "\n%{http_code}" -X "$metodo" "$url" -H "Content-Type: application/json" -d "$corpo")
    else
        RESPOSTA=$(curl -s -w "\n%{http_code}" -X "$metodo" "$url")
    fi
    STATUS=$(echo "$RESPOSTA" | tail -n 1)
    RESPOSTA=$(echo "$RESPOSTA" | sed '$d')
    [ -n "$RESPOSTA" ] && echo "$RESPOSTA"
    echo "< HTTP $STATUS"
}

# Extrai o primeiro "id" do JSON de resposta (o id do recurso criado)
extrair_id() {
    echo "$RESPOSTA" | grep -o '"id": *[0-9]*' | head -n 1 | grep -o '[0-9]*$'
}

# SELECT direto no banco (somente quando o container existe nesta maquina)
select_banco() {
    if command -v docker > /dev/null 2>&1 && docker ps --format '{{.Names}}' 2>/dev/null | grep -q '^nivelseguro-db$'; then
        echo "> SELECT direto no MySQL (container nivelseguro-db)"
        docker exec nivelseguro-db sh -c 'mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE" -t -e "'"$1"'" 2>/dev/null'
    fi
}

titulo "0) API no ar? (health check)"
requisicao GET "$BASE_URL/actuator/health"

# ======================= CREATE =======================
titulo "1) CREATE - POST /api/bairros"
requisicao POST "$BASE_URL/api/bairros" '{"nome":"Vila Teste FIAP","cidade":"São Paulo","uf":"SP","regiao":"Zona Sul"}'
BAIRRO_ID=$(extrair_id)
echo "Bairro criado: id=$BAIRRO_ID"

if [ -z "$BAIRRO_ID" ]; then
    echo "Falha ao criar o bairro. Verifique se a API esta no ar em $BASE_URL"
    exit 1
fi

titulo "2) CREATE - POST /api/sensores (relacionado ao bairro $BAIRRO_ID)"
requisicao POST "$BASE_URL/api/sensores" "{\"codigo\":\"$CODIGO_SENSOR\",\"localizacao\":\"Córrego de teste - ponto 1\",\"cotaAlertaCm\":200,\"cotaEmergenciaCm\":280,\"bairroId\":$BAIRRO_ID}"
SENSOR_ID=$(extrair_id)
echo "Sensor criado: id=$SENSOR_ID"

titulo "3) CREATE - POST /api/leituras (simula o Arduino enviando 230 cm -> ALERTA)"
requisicao POST "$BASE_URL/api/leituras" "{\"sensorId\":$SENSOR_ID,\"nivelAguaCm\":230.5,\"chuvaMm\":48.2}"
LEITURA_ID=$(extrair_id)
echo "Leitura criada: id=$LEITURA_ID"

# ======================= READ =======================
titulo "4) READ - GET por id e listas"
requisicao GET "$BASE_URL/api/bairros/$BAIRRO_ID"
requisicao GET "$BASE_URL/api/bairros/$BAIRRO_ID/sensores"
requisicao GET "$BASE_URL/api/sensores/$SENSOR_ID/leituras"
requisicao GET "$BASE_URL/api/leituras/alertas"

# ======================= UPDATE =======================
titulo "5) UPDATE - PUT /api/bairros/$BAIRRO_ID"
requisicao PUT "$BASE_URL/api/bairros/$BAIRRO_ID" '{"nome":"Vila Teste FIAP - ALTERADO","cidade":"São Paulo","uf":"SP","regiao":"Zona Sul"}'

titulo "6) UPDATE - PUT /api/leituras/$LEITURA_ID (nivel sobe para 300 cm -> EMERGENCIA)"
requisicao PUT "$BASE_URL/api/leituras/$LEITURA_ID" "{\"sensorId\":$SENSOR_ID,\"nivelAguaCm\":300,\"chuvaMm\":61.0}"

titulo "7) Persistencia: SELECT com JOIN das 3 tabelas"
select_banco "SELECT b.id AS bairro_id, b.nome AS bairro, s.id AS sensor_id, s.codigo, l.id AS leitura_id, l.nivel_agua_cm, l.nivel_risco FROM bairros b JOIN sensores s ON s.bairro_id = b.id JOIN leituras l ON l.sensor_id = s.id WHERE b.id = $BAIRRO_ID;"

# ======================= ERROS TRATADOS =======================
titulo "8) Validacao - POST invalido deve retornar 400"
requisicao POST "$BASE_URL/api/bairros" '{"nome":"","cidade":"São Paulo","uf":"SAO"}'

titulo "9) Recurso inexistente deve retornar 404"
requisicao GET "$BASE_URL/api/bairros/999999"

# ======================= DELETE =======================
titulo "10) DELETE - leitura, sensor e bairro"
requisicao DELETE "$BASE_URL/api/leituras/$LEITURA_ID"
requisicao DELETE "$BASE_URL/api/sensores/$SENSOR_ID"
requisicao DELETE "$BASE_URL/api/bairros/$BAIRRO_ID"
requisicao GET "$BASE_URL/api/bairros/$BAIRRO_ID"

titulo "11) SELECT apos o DELETE (o bairro de teste nao existe mais)"
select_banco "SELECT COUNT(*) AS bairros_com_id_$BAIRRO_ID FROM bairros WHERE id = $BAIRRO_ID;"

echo ""
echo "Teste do CRUD finalizado."
