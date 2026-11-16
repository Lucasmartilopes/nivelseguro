package br.com.fiap.nivelseguro.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record SensorRequest(

        @NotBlank(message = "O codigo do sensor e obrigatorio")
        @Size(max = 30, message = "O codigo deve ter no maximo 30 caracteres")
        String codigo,

        @NotBlank(message = "A localizacao e obrigatoria")
        @Size(max = 150, message = "A localizacao deve ter no maximo 150 caracteres")
        String localizacao,

        @NotNull(message = "A cota de alerta (cm) e obrigatoria")
        @DecimalMin(value = "1.0", message = "A cota de alerta deve ser maior que zero")
        BigDecimal cotaAlertaCm,

        @NotNull(message = "A cota de emergencia (cm) e obrigatoria")
        @DecimalMin(value = "1.0", message = "A cota de emergencia deve ser maior que zero")
        BigDecimal cotaEmergenciaCm,

        // Opcional: se nao informado, o sensor e criado como ativo
        Boolean ativo,

        @NotNull(message = "O id do bairro e obrigatorio")
        Long bairroId
) {
}
