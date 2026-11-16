#!/bin/bash
# ============================================================
# NivelSeguro - Evidencias tecnicas do ambiente (DENTRO da VM)
# Mostra tudo o que a entrega pede: containers, logs, acesso aos
# containers, usuario, diretorios, rede, volume e SELECT no banco.
#
#   ./scripts/vm/evidencias.sh
# ============================================================
cd "$(dirname "$0")/../.." || exit 1

secao() {
    echo ""
    echo "======================================================"
    echo " $1"
    echo "======================================================"
}

secao "1) Maquina: VM Azure"
echo "Usuario logado : $(whoami)"
echo "Hostname       : $(hostname)"
grep PRETTY_NAME /etc/os-release
free -m

secao "2) Estrutura de diretorios do projeto"
if command -v tree > /dev/null 2>&1; then
    tree -L 3 -I 'target|.git'
else
    find . -maxdepth 3 -not -path './.git*' -not -path './target*' | sort
fi

secao "3) Versoes do Docker e Docker Compose"
docker --version
docker compose version

secao "4) Containers em execucao"
docker compose ps
echo ""
docker ps --format 'table {{.Names}}\t{{.Image}}\t{{.Status}}\t{{.Ports}}'

secao "5) Imagens construidas (Dockerfile)"
docker images | grep -E 'REPOSITORY|nivelseguro'

secao "6) Rede: containers conectados na nivelseguro-network"
docker network inspect nivelseguro-network \
    --format '{{range .Containers}}{{.Name}} -> {{.IPv4Address}}{{println}}{{end}}'

secao "7) Volume nomeado do banco"
docker volume inspect nivelseguro-mysql-data

secao "8) Acesso ao container da API (docker exec)"
echo "> docker exec nivelseguro-api whoami"
docker exec nivelseguro-api whoami
echo "> docker exec nivelseguro-api id"
docker exec nivelseguro-api id
echo "> docker exec nivelseguro-api pwd   (WORKDIR)"
docker exec nivelseguro-api pwd
echo "> docker exec nivelseguro-api ls -la /app"
docker exec nivelseguro-api ls -la /app
echo "> Usuario configurado na imagem (USER do Dockerfile):"
docker inspect nivelseguro-api --format '{{.Config.User}}'
echo "> Variaveis de ambiente da API (sem senha):"
docker exec nivelseguro-api env | grep -E '^(SPRING_DATASOURCE_URL|SPRING_DATASOURCE_USERNAME|SERVER_PORT|JAVA_OPTS|TZ)='

secao "9) Acesso ao container do banco (docker exec)"
echo "> Processo do MySQL roda como o usuario:"
docker top nivelseguro-db -o user,pid,comm 2>/dev/null || docker top nivelseguro-db
echo "> Arquivos do banco dentro do volume (/var/lib/mysql):"
docker exec nivelseguro-db ls /var/lib/mysql | head -n 15

secao "10) Logs dos containers (ultimas 15 linhas)"
echo "--- nivelseguro-db ---"
docker logs --tail 15 nivelseguro-db 2>&1
echo ""
echo "--- nivelseguro-api ---"
docker logs --tail 15 nivelseguro-api 2>&1

secao "11) SELECT diretamente no banco"
docker exec -i nivelseguro-db sh -c 'mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE" -t 2>/dev/null' < database/consultas.sql

echo ""
echo "Evidencias coletadas."
