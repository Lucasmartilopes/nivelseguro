package br.com.fiap.nivelseguro.exception;

/** Lancada quando os dados sao validos, mas violam uma regra de negocio -> HTTP 400. */
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
