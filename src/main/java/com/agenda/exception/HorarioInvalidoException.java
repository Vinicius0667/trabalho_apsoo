package com.agenda.exception;

/** Horário fora do formato HH:mm ou atendimento que passaria da meia-noite. */
public class HorarioInvalidoException extends DadoInvalidoException {

    public HorarioInvalidoException(String mensagem) {
        super(mensagem);
    }
}
