package com.agenda.view;

import com.agenda.dao.UsuarioDAO;
import com.agenda.model.Profissional;
import com.agenda.util.MD5Util;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class LoginView extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtSenha;
    private JButton btnLogin;

    private JTextField txtTextoHash;
    private JTextField txtResultadoHash;
    private JButton btnGerarHash;

    private UsuarioDAO usuarioDAO;

    public LoginView() {
        usuarioDAO = new UsuarioDAO();
        initComponents();
    }

    private void initComponents() {
        setTitle("Login - Sistema de Agendamento");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBorder(BorderFactory.createTitledBorder("Login de Profissional"));
        GridBagConstraints grid = new GridBagConstraints();
        grid.insets = new Insets(5, 5, 5, 5);
        grid.fill = GridBagConstraints.HORIZONTAL;

        grid.gridx = 0;
        grid.gridy = 0;
        loginPanel.add(new JLabel("Nome:"), grid);

        grid.gridx = 1;
        grid.gridy = 0;
        grid.weightx = 1.0;
        txtUsuario = new JTextField();
        loginPanel.add(txtUsuario, grid);

        grid.gridx = 0;
        grid.gridy = 1;
        grid.weightx = 0;
        loginPanel.add(new JLabel("Senha:"), grid);

        grid.gridx = 1;
        grid.gridy = 1;
        grid.weightx = 1.0;
        txtSenha = new JPasswordField();
        loginPanel.add(txtSenha, grid);

        grid.gridx = 1;
        grid.gridy = 2;
        grid.weightx = 0;
        grid.anchor = GridBagConstraints.EAST;
        btnLogin = new JButton("Login");
        btnLogin.addActionListener(e -> realizarLogin());
        loginPanel.add(btnLogin, grid);

        // Gerar Hash
        // JPanel hashPanel = new JPanel(new GridBagLayout());
        // hashPanel.setBorder(BorderFactory.createTitledBorder("Gerador de Hash MD5"));
        // GridBagConstraints gridHash = new GridBagConstraints();
        // gridHash.insets = new Insets(5, 5, 5, 5);
        // gridHash.fill = GridBagConstraints.HORIZONTAL;

        // gridHash.gridx = 0;
        // gridHash.gridy = 0;
        // hashPanel.add(new JLabel("Texto:"), gridHash);

        // gridHash.gridx = 1;
        // gridHash.gridy = 0;
        // gridHash.weightx = 1.0;
        // txtTextoHash = new JTextField();
        // hashPanel.add(txtTextoHash, gridHash);

        // gridHash.gridx = 1;
        // gridHash.gridy = 1;
        // gridHash.weightx = 0;
        // gridHash.anchor = GridBagConstraints.EAST;
        // btnGerarHash = new JButton("Gerar Hash");
        // btnGerarHash.addActionListener(e -> gerarHash());
        // hashPanel.add(btnGerarHash, gridHash);

        // gridHash.gridx = 0;
        // gridHash.gridy = 2;
        // gridHash.weightx = 0;
        // gridHash.anchor = GridBagConstraints.WEST;
        // hashPanel.add(new JLabel("MD5:"), gridHash);

        // gridHash.gridx = 1;
        // gridHash.gridy = 2;
        // gridHash.weightx = 1.0;
        // txtResultadoHash = new JTextField();
        // txtResultadoHash.setEditable(false);
        // hashPanel.add(txtResultadoHash, gridHash);

        mainPanel.add(loginPanel);
        // mainPanel.add(hashPanel);

        add(mainPanel);
    }

    private void realizarLogin() {
        String nome = txtUsuario.getText().trim();
        String senha = new String(txtSenha.getPassword());

        if (nome.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos de login.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String senhaMD5 = MD5Util.getMD5Hash(senha);

        try {
            Profissional profissional = usuarioDAO.autenticarProfissional(nome, senhaMD5);
            if (profissional != null) {
                JOptionPane.showMessageDialog(this, "Login efetuado com sucesso!\nBem-vindo(a), " + profissional.getNome(), "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
                // Apenas abrindo AgendamentosView conforme solicitado
                new AgendamentosView().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Usuário ou senha inválidos, ou profissional inativo.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao conectar com o banco de dados.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void gerarHash() {
        String texto = txtTextoHash.getText();
        if (texto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite um texto para gerar o hash.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String hash = MD5Util.getMD5Hash(texto);
        txtResultadoHash.setText(hash);
    }
}
