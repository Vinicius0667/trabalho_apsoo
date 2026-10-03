package com.agenda.exception;

/** O profissional já possui outro agendamento nesse horário. */
public class HorarioIndisponivelException extends DadoInvalidoException {

    public HorarioIndisponivelException(String mensagem) {
        super(mensagem);
    }
}
