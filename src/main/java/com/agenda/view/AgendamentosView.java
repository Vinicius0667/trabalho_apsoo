package com.agenda.view;

import com.agenda.controller.AgendamentoControladora;
import com.agenda.model.Agendamento;
import com.agenda.model.ItemAgendamento;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.StringJoiner;

public class AgendamentosView extends JFrame {
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    private DefaultTableModel modeloTabela;

    private final AgendamentoControladora controller;

    public AgendamentosView() {
        controller = new AgendamentoControladora();
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setTitle("Agenda - Agendamentos");
        setSize(950, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));

        JLabel titulo = new JLabel("Agendamentos cadastrados", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        add(titulo, BorderLayout.NORTH);

        modeloTabela = new DefaultTableModel(
                new String[]{"ID", "Cliente", "Profissional", "Data", "Início", "Fim", "Serviços", "Valor", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tabela = new JTable(modeloTabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getColumnModel().getColumn(0).setMaxWidth(50);
        tabela.getColumnModel().getColumn(6).setPreferredWidth(200);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel rodape = new JPanel(new BorderLayout());
        JButton btnAdmin = new JButton("Área do administrador");
        JButton btnAtualizar = new JButton("Atualizar");
        JButton btnNovo = new JButton("Novo agendamento");

        JPanel esquerda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        esquerda.add(btnAdmin);
        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        direita.add(btnAtualizar);
        direita.add(btnNovo);
        rodape.add(esquerda, BorderLayout.WEST);
        rodape.add(direita, BorderLayout.EAST);
        add(rodape, BorderLayout.SOUTH);

        btnAdmin.addActionListener(e -> abrir(new AdminView(this)));
        btnAtualizar.addActionListener(e -> atualizarTabela());
        btnNovo.addActionListener(e -> abrir(new CadastroAgendamentoView(this)));
    }

    @Override
    public void setVisible(boolean visivel) {
        if (visivel) {
            atualizarTabela();
        }
        super.setVisible(visivel);
    }

    private void abrir(JFrame tela) {
        setVisible(false);
        tela.setVisible(true);
    }

    private void atualizarTabela() {
        modeloTabela.setRowCount(0);
        try {
            for (Agendamento a : controller.listarTodosAgendamentos()) {
                modeloTabela.addRow(new Object[]{
                        a.getId(),
                        a.getCliente().getNome(),
                        a.getProfissional().getNome(),
                        a.getDataAgendada().format(FORMATO_DATA),
                        a.getHorarioInicio().format(FORMATO_HORA),
                        a.getHorarioFim().format(FORMATO_HORA),
                        nomesServicos(a),
                        String.format("R$ %.2f", a.getValorTotal()),
                        a.getStatus()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String nomesServicos(Agendamento agendamento) {
        StringJoiner nomes = new StringJoiner(", ");
        for (ItemAgendamento item : agendamento.getItens()) {
            nomes.add(item.getServico().getNome());
        }
        return nomes.toString();
    }
}
