-- ============================================================
-- Consultas para evidenciar os dados DIRETAMENTE no banco
-- Uso (dentro da VM, na pasta do projeto):
--   docker exec -i nivelseguro-db sh -c 'mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE"' < database/consultas.sql
-- ============================================================

SHOW TABLES;

-- Tabela 1
SELECT * FROM bairros;

-- Tabela 2
SELECT * FROM sensores;

-- Tabela 3 (ultimas 15 leituras)
SELECT * FROM leituras ORDER BY id DESC LIMIT 15;

-- Relacionamento entre as 3 tabelas (JOIN)
SELECT b.nome        AS bairro,
       s.codigo      AS sensor,
       l.nivel_agua_cm,
       s.cota_alerta_cm,
       l.nivel_risco,
       l.data_hora
  FROM leituras l
  JOIN sensores s ON s.id = l.sensor_id
  JOIN bairros  b ON b.id = s.bairro_id
 ORDER BY l.data_hora DESC
 LIMIT 15;

-- Quantidade de alertas por bairro
SELECT b.nome AS bairro, COUNT(*) AS total_alertas
  FROM leituras l
  JOIN sensores s ON s.id = l.sensor_id
  JOIN bairros  b ON b.id = s.bairro_id
 WHERE l.nivel_risco IN ('ALERTA', 'EMERGENCIA')
 GROUP BY b.nome
 ORDER BY total_alertas DESC;
