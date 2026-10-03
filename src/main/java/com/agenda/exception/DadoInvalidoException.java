package com.agenda.exception;

/**
 * Classe base para todos os erros de dados digitados incorretamente no cadastro.
 * A tela captura essa exceção e mostra a mensagem para o usuário corrigir o campo.
 */
public class DadoInvalidoException extends Exception {

    public DadoInvalidoException(String mensagem) {
        super(mensagem);
    }
}
