package com.agenda.exception;

/** Nome do cliente vazio, muito longo ou com caracteres inválidos. */
public class NomeClienteInvalidoException extends DadoInvalidoException {

    public NomeClienteInvalidoException(String mensagem) {
        super(mensagem);
    }
}
