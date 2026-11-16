package br.com.fiap.nivelseguro.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LeituraRequest(

        @NotNull(message = "O id do sensor e obrigatorio")
        Long sensorId,

        @NotNull(message = "O nivel da agua (cm) e obrigatorio")
        @DecimalMin(value = "0.0", message = "O nivel da agua nao pode ser negativo")
        BigDecimal nivelAguaCm,

        @DecimalMin(value = "0.0", message = "A chuva (mm) nao pode ser negativa")
        BigDecimal chuvaMm,

        // Opcional: se nao informado, a API usa a data/hora atual
        @PastOrPresent(message = "A data/hora da leitura nao pode estar no futuro")
        LocalDateTime dataHora
) {
}
