# ============================================================
# Imagem do banco de dados NivelSeguro (MySQL 8.0)
# Credenciais NAO ficam na imagem: sao passadas por variaveis
# de ambiente no docker-compose.yml (arquivo .env).
# ============================================================
FROM mysql:8.0

# Fuso horario do container
ENV TZ=America/Sao_Paulo

# Script executado automaticamente na 1a inicializacao (volume vazio)
COPY init.sql /docker-entrypoint-initdb.d/01-init.sql

# Porta padrao do MySQL
EXPOSE 3306
