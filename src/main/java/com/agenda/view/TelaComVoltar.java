package com.agenda.view;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Base das telas que são abertas a partir de outra.
 * Guarda a tela anterior e oferece o botão "Voltar" (fechar no X também volta).
 */
public abstract class TelaComVoltar extends JFrame {

    private final JFrame telaAnterior;

    protected TelaComVoltar(String titulo, JFrame telaAnterior) {
        super(titulo);
        this.telaAnterior = telaAnterior;

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                voltar();
            }
        });
    }

    protected void voltar() {
        dispose();
        telaAnterior.setVisible(true);
    }

    protected JButton criarBotaoVoltar() {
        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.addActionListener(e -> voltar());
        return btnVoltar;
    }
}
