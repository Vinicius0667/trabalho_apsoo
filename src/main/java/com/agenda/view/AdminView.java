package com.agenda.view;

import com.agenda.controller.AdminControladora;
import com.agenda.model.Administrador;

import javax.swing.*;
import java.awt.*;

public class AdminView extends JFrame {
    private final AdminControladora controller;

    public AdminView() {
        super("Agenda - Área do administrador");
        controller = new AdminControladora();
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setSize(450, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));

        JPanel perfil = new JPanel(new GridBagLayout());
        perfil.setBorder(BorderFactory.createTitledBorder("Perfil do administrador"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        try {
            Administrador admin = controller.getAdministrador();
            if (admin == null) {
                perfil.add(new JLabel("Nenhum administrador cadastrado."), gbc);
            } else {
                adicionarLinha(perfil, gbc, 0, "Nome:", admin.getNome());
                adicionarLinha(perfil, gbc, 1, "CPF:", admin.getCpf());
                adicionarLinha(perfil, gbc, 2, "Telefone:", admin.getTelefone());
                adicionarLinha(perfil, gbc, 3, "E-mail:", admin.getEmail());
                adicionarLinha(perfil, gbc, 4, "Perfil:", "Administrador");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
        add(perfil, BorderLayout.CENTER);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.addActionListener(e -> dispose());
        botoes.add(btnVoltar);
        add(botoes, BorderLayout.SOUTH);
    }

    private void adicionarLinha(JPanel painel, GridBagConstraints gbc, int linha, String rotulo, String valor) {
        gbc.gridx = 0; gbc.gridy = linha;
        JLabel lblRotulo = new JLabel(rotulo);
        lblRotulo.setFont(lblRotulo.getFont().deriveFont(Font.BOLD));
        painel.add(lblRotulo, gbc);
        gbc.gridx = 1;
        painel.add(new JLabel(valor == null ? "-" : valor), gbc);
    }
}
