-- ============================================================
-- NivelSeguro - criacao das tabelas e carga inicial
-- Executado AUTOMATICAMENTE pelo container MySQL na primeira
-- inicializacao (quando o volume nivelseguro-mysql-data esta vazio).
--
-- Modelo:  bairros (1) ---< (N) sensores (1) ---< (N) leituras
-- ============================================================
-- O entrypoint do MySQL ja executa este script dentro do banco
-- definido em MYSQL_DATABASE (arquivo .env), por isso nao ha "USE".
SET NAMES utf8mb4;

-- ------------------------------------------------------------
-- Tabela 1: bairros monitorados
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS bairros (
    id      BIGINT       NOT NULL AUTO_INCREMENT,
    nome    VARCHAR(100) NOT NULL,
    cidade  VARCHAR(100) NOT NULL,
    uf      VARCHAR(2)   NOT NULL,
    regiao  VARCHAR(50)  NULL,
    CONSTRAINT pk_bairros PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- Tabela 2: sensores de nivel de agua (N sensores -> 1 bairro)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sensores (
    id                  BIGINT        NOT NULL AUTO_INCREMENT,
    codigo              VARCHAR(30)   NOT NULL,
    localizacao         VARCHAR(150)  NOT NULL,
    cota_alerta_cm      DECIMAL(7,2)  NOT NULL,
    cota_emergencia_cm  DECIMAL(7,2)  NOT NULL,
    ativo               BOOLEAN       NOT NULL DEFAULT TRUE,
    bairro_id           BIGINT        NOT NULL,
    CONSTRAINT pk_sensores        PRIMARY KEY (id),
    CONSTRAINT uk_sensores_codigo UNIQUE (codigo),
    CONSTRAINT fk_sensores_bairro FOREIGN KEY (bairro_id)
        REFERENCES bairros (id) ON DELETE CASCADE,
    CONSTRAINT ck_sensores_cotas  CHECK (cota_emergencia_cm > cota_alerta_cm)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- Tabela 3: leituras enviadas pelos sensores (N leituras -> 1 sensor)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS leituras (
    id             BIGINT        NOT NULL AUTO_INCREMENT,
    sensor_id      BIGINT        NOT NULL,
    nivel_agua_cm  DECIMAL(7,2)  NOT NULL,
    chuva_mm       DECIMAL(6,2)  NULL,
    nivel_risco    VARCHAR(20)   NOT NULL,
    data_hora      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_leituras        PRIMARY KEY (id),
    CONSTRAINT fk_leituras_sensor FOREIGN KEY (sensor_id)
        REFERENCES sensores (id) ON DELETE CASCADE,
    CONSTRAINT ck_leituras_risco  CHECK (nivel_risco IN ('NORMAL', 'ATENCAO', 'ALERTA', 'EMERGENCIA')),
    INDEX idx_leituras_sensor_data (sensor_id, data_hora)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- Carga inicial (dados de teste)
-- ============================================================
INSERT INTO bairros (nome, cidade, uf, regiao) VALUES
    ('Jardim Pantanal', 'São Paulo',       'SP', 'Zona Leste'),
    ('Jardim Romano',   'São Paulo',       'SP', 'Zona Leste'),
    ('Vila Itaim',      'São Paulo',       'SP', 'Zona Leste'),
    ('Centro',          'Franco da Rocha', 'SP', 'Grande São Paulo'),
    ('Sarandi',         'Porto Alegre',    'RS', 'Zona Norte');

INSERT INTO sensores (codigo, localizacao, cota_alerta_cm, cota_emergencia_cm, ativo, bairro_id) VALUES
    ('NS-SP-001',  'Margem do Rio Tietê - ponto 1',  250.00, 320.00, TRUE,  1),
    ('NS-SP-002',  'Córrego - ponto de travessia',   180.00, 240.00, TRUE,  2),
    ('NS-SP-003',  'Várzea do Rio Tietê - ponto 2',  200.00, 260.00, TRUE,  3),
    ('NS-FR-001',  'Rio Juquery - ponte central',    300.00, 380.00, TRUE,  4),
    ('NS-POA-001', 'Arroio - margem norte',          220.00, 300.00, TRUE,  5),
    ('NS-SP-004',  'Margem do Rio Tietê - ponto 3',  250.00, 320.00, FALSE, 1);

-- nivel_risco segue a regra da API:
--   >= cota_emergencia -> EMERGENCIA | >= cota_alerta -> ALERTA
--   >= 70% da cota_alerta -> ATENCAO | abaixo -> NORMAL
INSERT INTO leituras (sensor_id, nivel_agua_cm, chuva_mm, nivel_risco, data_hora) VALUES
    (1, 120.50,  2.00, 'NORMAL',     '2026-11-10 08:00:00'),
    (1, 190.00, 18.40, 'ATENCAO',    '2026-11-10 14:00:00'),
    (1, 262.30, 41.70, 'ALERTA',     '2026-11-10 18:30:00'),
    (2,  95.00,  1.20, 'NORMAL',     '2026-11-11 07:15:00'),
    (2, 150.20, 22.00, 'ATENCAO',    '2026-11-11 16:45:00'),
    (2, 245.80, 55.30, 'EMERGENCIA', '2026-11-11 19:10:00'),
    (3,  80.00,  0.00, 'NORMAL',     '2026-11-12 09:00:00'),
    (3, 210.00, 35.60, 'ALERTA',     '2026-11-12 17:20:00'),
    (4, 150.00,  5.10, 'NORMAL',     '2026-11-13 10:00:00'),
    (4, 390.50, 72.80, 'EMERGENCIA', '2026-11-13 21:40:00'),
    (5, 100.00,  3.00, 'NORMAL',     '2026-11-14 06:30:00'),
    (5, 160.00, 19.90, 'ATENCAO',    '2026-11-14 13:00:00');
