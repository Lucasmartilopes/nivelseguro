package br.com.fiap.nivelseguro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BairroRequest(

        @NotBlank(message = "O nome do bairro e obrigatorio")
        @Size(max = 100, message = "O nome deve ter no maximo 100 caracteres")
        String nome,

        @NotBlank(message = "A cidade e obrigatoria")
        @Size(max = 100, message = "A cidade deve ter no maximo 100 caracteres")
        String cidade,

        @NotBlank(message = "A UF e obrigatoria")
        @Pattern(regexp = "^[A-Za-z]{2}$", message = "A UF deve ter 2 letras (ex.: SP)")
        String uf,

        @Size(max = 50, message = "A regiao deve ter no maximo 50 caracteres")
        String regiao
) {
}
