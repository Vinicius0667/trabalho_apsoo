package com.agenda.exception;

/** Data do agendamento anterior ao dia de hoje. */
public class DataPassadaException extends DadoInvalidoException {

    public DataPassadaException(String mensagem) {
        super(mensagem);
    }
}
