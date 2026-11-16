package br.com.fiap.nivelseguro.dto;

import br.com.fiap.nivelseguro.model.Leitura;
import br.com.fiap.nivelseguro.model.NivelRisco;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LeituraResponse(
        Long id,
        Long sensorId,
        String sensorCodigo,
        String bairroNome,
        BigDecimal nivelAguaCm,
        BigDecimal chuvaMm,
        NivelRisco nivelRisco,
        LocalDateTime dataHora
) {
    public static LeituraResponse de(Leitura leitura) {
        return new LeituraResponse(
                leitura.getId(),
                leitura.getSensor().getId(),
                leitura.getSensor().getCodigo(),
                leitura.getSensor().getBairro().getNome(),
                leitura.getNivelAguaCm(),
                leitura.getChuvaMm(),
                leitura.getNivelRisco(),
                leitura.getDataHora()
        );
    }
}
