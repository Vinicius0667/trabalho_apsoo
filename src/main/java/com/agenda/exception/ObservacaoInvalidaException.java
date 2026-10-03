package com.agenda.exception;

/** Observação maior que o tamanho permitido no banco. */
public class ObservacaoInvalidaException extends DadoInvalidoException {

    public ObservacaoInvalidaException(String mensagem) {
        super(mensagem);
    }
}
