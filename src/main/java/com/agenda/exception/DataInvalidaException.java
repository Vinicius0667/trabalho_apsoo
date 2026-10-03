package com.agenda.exception;

/** Data digitada fora do formato dd/MM/aaaa ou inexistente (ex.: 31/02). */
public class DataInvalidaException extends DadoInvalidoException {

    public DataInvalidaException(String mensagem) {
        super(mensagem);
    }
}
