package com.agenda.exception;

/** Nenhum profissional foi escolhido para o atendimento. */
public class ProfissionalNaoSelecionadoException extends DadoInvalidoException {

    public ProfissionalNaoSelecionadoException(String mensagem) {
        super(mensagem);
    }
}
