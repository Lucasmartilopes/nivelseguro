package br.com.fiap.nivelseguro.dto;

import br.com.fiap.nivelseguro.model.Bairro;

public record BairroResponse(
        Long id,
        String nome,
        String cidade,
        String uf,
        String regiao,
        int totalSensores
) {
    public static BairroResponse de(Bairro bairro) {
        return new BairroResponse(
                bairro.getId(),
                bairro.getNome(),
                bairro.getCidade(),
                bairro.getUf(),
                bairro.getRegiao(),
                bairro.getSensores().size()
        );
    }
}
