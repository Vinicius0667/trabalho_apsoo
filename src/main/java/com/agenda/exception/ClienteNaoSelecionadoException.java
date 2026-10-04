package com.agenda.exception;

public class ClienteNaoSelecionadoException extends DadoInvalidoException {
    public ClienteNaoSelecionadoException(String mensagem) {
        super(mensagem);
    }
}
