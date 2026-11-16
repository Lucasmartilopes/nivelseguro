package br.com.fiap.nivelseguro.dto;

import br.com.fiap.nivelseguro.model.Sensor;

import java.math.BigDecimal;

public record SensorResponse(
        Long id,
        String codigo,
        String localizacao,
        BigDecimal cotaAlertaCm,
        BigDecimal cotaEmergenciaCm,
        Boolean ativo,
        Long bairroId,
        String bairroNome
) {
    public static SensorResponse de(Sensor sensor) {
        return new SensorResponse(
                sensor.getId(),
                sensor.getCodigo(),
                sensor.getLocalizacao(),
                sensor.getCotaAlertaCm(),
                sensor.getCotaEmergenciaCm(),
                sensor.getAtivo(),
                sensor.getBairro().getId(),
                sensor.getBairro().getNome()
        );
    }
}
