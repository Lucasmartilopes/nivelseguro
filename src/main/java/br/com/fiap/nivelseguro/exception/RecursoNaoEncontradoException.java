package br.com.fiap.nivelseguro.exception;

/** Lancada quando um id nao existe no banco -> HTTP 404. */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
