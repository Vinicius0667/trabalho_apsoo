package com.agenda.exception;

/** Horário que já passou, quando o agendamento é para hoje. */
public class HorarioPassadoException extends DadoInvalidoException {

    public HorarioPassadoException(String mensagem) {
        super(mensagem);
    }
}
