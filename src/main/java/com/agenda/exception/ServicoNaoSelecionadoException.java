package com.agenda.exception;

/** Nenhum serviço foi selecionado para o agendamento. */
public class ServicoNaoSelecionadoException extends DadoInvalidoException {

    public ServicoNaoSelecionadoException(String mensagem) {
        super(mensagem);
    }
}
