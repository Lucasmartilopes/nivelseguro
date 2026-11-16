package br.com.fiap.nivelseguro.model;

import java.math.BigDecimal;

/**
 * Classificacao de risco de uma leitura de nivel de agua.
 *
 * Regra de negocio (calculada pela API a cada leitura recebida do sensor):
 *   nivel >= cota de emergencia          -> EMERGENCIA
 *   nivel >= cota de alerta              -> ALERTA
 *   nivel >= 70% da cota de alerta       -> ATENCAO
 *   abaixo disso                         -> NORMAL
 */
public enum NivelRisco {
    NORMAL,
    ATENCAO,
    ALERTA,
    EMERGENCIA;

    private static final BigDecimal PERCENTUAL_ATENCAO = new BigDecimal("0.70");

    public static NivelRisco calcular(BigDecimal nivelAguaCm, BigDecimal cotaAlertaCm, BigDecimal cotaEmergenciaCm) {
        if (nivelAguaCm.compareTo(cotaEmergenciaCm) >= 0) {
            return EMERGENCIA;
        }
        if (nivelAguaCm.compareTo(cotaAlertaCm) >= 0) {
            return ALERTA;
        }
        if (nivelAguaCm.compareTo(cotaAlertaCm.multiply(PERCENTUAL_ATENCAO)) >= 0) {
            return ATENCAO;
        }
        return NORMAL;
    }
}
