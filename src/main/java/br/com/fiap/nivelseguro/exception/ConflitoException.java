package br.com.fiap.nivelseguro.exception;

/** Lancada quando o registro ja existe (ex.: codigo de sensor duplicado) -> HTTP 409. */
public class ConflitoException extends RuntimeException {

    public ConflitoException(String mensagem) {
        super(mensagem);
    }
}
