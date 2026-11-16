# ============================================================
# Imagem da API NivelSeguro (Java 17 + Spring Boot)
# Build multi-stage: o Maven so existe no estagio de build;
# a imagem final leva apenas o JRE e o .jar (imagem menor e mais segura).
# ============================================================

# ---------- Estagio 1: build com Maven ----------
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build

# Copia o pom.xml primeiro e baixa as dependencias (aproveita o cache de camadas)
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Copia o codigo-fonte e gera o .jar (testes ignorados no build da imagem)
COPY src ./src
RUN mvn -B clean package -DskipTests

# ---------- Estagio 2: execucao ----------
FROM eclipse-temurin:17-jre

# Usuario NAO privilegiado para executar a aplicacao
RUN groupadd --system appgroup \
    && useradd --system --gid appgroup --no-create-home --shell /usr/sbin/nologin appuser

# Diretorio de trabalho da aplicacao
WORKDIR /app

# Copia somente o .jar gerado no estagio 1, ja pertencendo ao appuser
COPY --from=builder --chown=appuser:appgroup /build/target/nivelseguro-api.jar app.jar

# Variaveis de ambiente (podem ser sobrescritas no docker-compose.yml)
ENV SERVER_PORT=8080 \
    TZ=America/Sao_Paulo \
    JAVA_OPTS="-Xms128m -Xmx256m"

# A partir daqui tudo roda como appuser (nao-root)
USER appuser

# Porta da API
EXPOSE 8080

# "exec" faz o Java ser o PID 1 e receber o sinal de parada do Docker
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
