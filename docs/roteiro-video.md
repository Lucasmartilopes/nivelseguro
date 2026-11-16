# 🎥 Roteiro do vídeo — DevOps Tools & Cloud Computing

**Regras da entrega:** YouTube **não listado**, **mínimo 720p** (grave em 1080p), **com narração por voz**. Vídeo abaixo de 720p, ilegível ou sem explicação perde **20 pontos**.

**Antes de gravar**
- Aumente a fonte do terminal (Ctrl + `+`) para ficar legível em 720p.
- A VM precisa estar **ligada**, com o ambiente **já buildado** (o build demora; não grave a espera).
- Para mostrar a criação da VM, grave a execução dos scripts 01 e 02 antes e corte a espera na edição, ou mostre no portal os recursos já criados junto com a saída dos scripts.
- Deixe abertos: Cloud Shell, um terminal SSH na VM, o Postman (ou navegador) **no seu computador** e o repositório no GitHub.
- Teste o link do vídeo numa **aba anônima** antes de entregar.

Duração sugerida: **8 a 10 minutos.** Divida as falas entre os integrantes.

| Tempo | Tela | O que mostrar / falar |
|---|---|---|
| 0:00 – 0:40 | Slide/README | Nome da solução (NívelSeguro), integrantes, problema (enchentes), cidadão beneficiado, ODS 11 e 13. |
| 0:40 – 1:40 | `docs/arquitetura.png` | Explicar o fluxo: sensor/app → internet → IP público → NSG (22 e 8080; 3306 fechada) → VM AlmaLinux → Docker → API → rede → MySQL → volume. |
| 1:40 – 3:00 | Cloud Shell | Mostrar `scripts/azure/01-criar-vm.sh` e a saída da execução (`az group create`, `az vm create`, `az network nsg rule create`). Mostrar `02-configurar-vm.sh` (`az vm run-command` instalando Docker e swap). Rodar `az vm list -d -o table` para provar que a VM existe e está rodando. |
| 3:00 – 4:00 | SSH na VM | `ssh admlnx@IP` · `hostname` · `cat /etc/os-release` · `docker --version` · `docker compose version` · `tree -L 2` (estrutura de diretórios). |
| 4:00 – 5:00 | VM: arquivos | `cat Dockerfile`: explicar multi-stage, **WORKDIR /app**, **USER appuser**, **ENV**, **EXPOSE 8080**. `cat docker-compose.yml`: explicar os 2 serviços, **network**, **volume**, **ports**, **environment** (vindo do `.env`), **depends_on + healthcheck**. |
| 5:00 – 5:40 | VM | `docker compose up -d --build` (se já estiver no ar, mostra "Running") · `docker compose ps` (os dois Up, o banco `healthy`) · `docker images \| grep nivelseguro`. |
| 5:40 – 6:30 | VM | **Logs:** `docker logs --tail 20 nivelseguro-db` e `docker logs --tail 20 nivelseguro-api`. **Acesso:** `docker exec -it nivelseguro-api sh`, depois `whoami` (appuser), `pwd` (/app), `ls -la`, `exit`. `docker volume inspect nivelseguro-mysql-data` · `docker network inspect nivelseguro-network`. |
| 6:30 – 8:00 | **Postman no seu PC** (IP público!) | Mostrar que a URL é `http://IP-PUBLICO:8080` (não localhost). **POST** bairro → **POST** sensor → **POST** leitura de 230 cm (a API devolve `ALERTA`) → **GET** listas e `/api/leituras/alertas` → **PUT** bairro → **DELETE** leitura. Mostrar um **400** (bairro sem nome) e um **404** (id 999999). |
| 8:00 – 8:45 | VM | **SELECT direto no banco:** `docker exec -it nivelseguro-db mysql -u nivelseguro -p nivelseguro` e depois `SELECT * FROM bairros;`, `SELECT * FROM sensores;`, `SELECT * FROM leituras ORDER BY id DESC;` e o JOIN das 3 tabelas. Mostrar que os registros criados pelo Postman estão lá. |
| 8:45 – 9:30 | VM | **Persistência:** `docker compose down` → `docker ps -a` (nada) → `docker volume ls` (volume continua) → `docker compose up -d` → SELECT de novo: os dados continuam. |
| 9:30 – 10:00 | GitHub | Repositório público: código, Dockerfile, docker-compose.yml, scripts Azure CLI, arquitetura e README/How To. Encerramento. |

> 💡 Atalho: `./scripts/vm/evidencias.sh` mostra logs, exec, usuário, rede, volume e SELECT numa única execução, mas no vídeo é melhor digitar os comandos principais e explicar cada um.

**Perguntas prováveis na apresentação presencial (todos devem saber responder)**
- Por que usar um Dockerfile multi-stage? → a imagem final leva só o JRE e o .jar: fica menor e não carrega Maven nem código-fonte.
- Por que um usuário não-root? → se a aplicação for invadida, o atacante não tem privilégio de root dentro do container.
- Como a API encontra o banco? → pelo **nome do serviço** (`nivelseguro-db`), resolvido pelo DNS interno da rede `nivelseguro-network`.
- O que acontece com os dados num `docker compose down`? E num `down -v`? → no `down` os dados ficam no volume nomeado; no `down -v` o volume é apagado e os dados se perdem.
- Para que serve o `depends_on` com `service_healthy`? → a API só sobe depois que o MySQL responde ao healthcheck.
- Por que a porta 3306 não está aberta no NSG? → o banco só é acessado pela API, dentro da rede do Docker; expor o banco na internet é um risco.
- Onde ficam as senhas? → no `.env`, que não vai para o GitHub; o Compose lê esse arquivo e injeta os valores como variáveis de ambiente.
- Diferença entre `EXPOSE` e `ports`? → `EXPOSE` só documenta a porta na imagem; `ports` publica a porta do container no host.
