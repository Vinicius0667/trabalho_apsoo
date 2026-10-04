package com.agenda.view;

import com.agenda.controller.ProfissionalControladora;
import com.agenda.model.Agendamento;
import com.agenda.model.Cliente;
import com.agenda.model.Profissional;
import com.agenda.model.Servico;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ProfissionalView extends JFrame {
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    private JComboBox<Profissional> cbProfissional;
    private JComboBox<Cliente> cbCliente;
    private JTextField txtData;
    private JTextField txtHora;
    private JList<Servico> listServicos;
    private JTextField txtObservacoes;
    private JTable tabela;
    private DefaultTableModel modeloTabela;

    private final ProfissionalControladora controller;

    public ProfissionalView() {
        controller = new ProfissionalControladora();
        inicializarComponentes();
        carregarDados();
    }

    private void inicializarComponentes() {
        setTitle("Agenda - Profissional");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Novo agendamento"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cbProfissional = new JComboBox<>();
        cbCliente = new JComboBox<>();
        txtData = new JTextField(LocalDate.now().format(FORMATO_DATA), 10);
        txtHora = new JTextField("09:00", 5);
        listServicos = new JList<>();
        listServicos.setVisibleRowCount(4);
        txtObservacoes = new JTextField(20);

        adicionarCampo(form, gbc, 0, "Profissional:", cbProfissional);
        adicionarCampo(form, gbc, 1, "Cliente:", cbCliente);
        adicionarCampo(form, gbc, 2, "Data (dd/MM/aaaa):", txtData);
        adicionarCampo(form, gbc, 3, "Hora (HH:mm):", txtHora);
        adicionarCampo(form, gbc, 4, "Serviços (Ctrl p/ vários):", new JScrollPane(listServicos));
        adicionarCampo(form, gbc, 5, "Observações:", txtObservacoes);

        JButton btnAgendar = new JButton("Agendar");
        gbc.gridx = 1; gbc.gridy = 6;
        form.add(btnAgendar, gbc);

        add(form, BorderLayout.NORTH);

        modeloTabela = new DefaultTableModel(
                new String[]{"ID", "Cliente", "Data", "Início", "Fim", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabela = new JTable(modeloTabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCancelar = new JButton("Cancelar agendamento");
        JButton btnConcluir = new JButton("Concluir agendamento");
        botoes.add(btnCancelar);
        botoes.add(btnConcluir);
        add(botoes, BorderLayout.SOUTH);

        btnAgendar.addActionListener(e -> agendar());
        btnCancelar.addActionListener(e -> cancelar());
        btnConcluir.addActionListener(e -> concluir());
        cbProfissional.addActionListener(e -> atualizarTabela());
    }

    private void adicionarCampo(JPanel painel, GridBagConstraints gbc, int linha, String rotulo, JComponent campo) {
        gbc.gridx = 0; gbc.gridy = linha;
        painel.add(new JLabel(rotulo), gbc);
        gbc.gridx = 1;
        painel.add(campo, gbc);
    }

    private void carregarDados() {
        try {
            for (Profissional p : controller.listarProfissionais()) {
                cbProfissional.addItem(p);
            }
            for (Cliente c : controller.listarClientes()) {
                cbCliente.addItem(c);
            }
            listServicos.setListData(controller.listarServicos().toArray(new Servico[0]));
        } catch (Exception ex) {
            mostrarErro(ex);
        }
        atualizarTabela();
    }

    private void atualizarTabela() {
        modeloTabela.setRowCount(0);
        Profissional profissional = (Profissional) cbProfissional.getSelectedItem();
        if (profissional == null) {
            return;
        }
        try {
            for (Agendamento a : controller.listarAgendamentos(profissional)) {
                modeloTabela.addRow(new Object[]{
                        a.getId(),
                        a.getCliente().getNome(),
                        a.getDataAgendada().format(FORMATO_DATA),
                        a.getHorarioInicio().format(FORMATO_HORA),
                        a.getHorarioFim().format(FORMATO_HORA),
                        a.getStatus()
                });
            }
        } catch (Exception ex) {
            mostrarErro(ex);
        }
    }

    private void agendar() {
        LocalDate data;
        LocalTime hora;
        try {
            data = LocalDate.parse(txtData.getText().trim(), FORMATO_DATA);
            hora = LocalTime.parse(txtHora.getText().trim(), FORMATO_HORA);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Data ou hora em formato inválido!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Servico> servicos = listServicos.getSelectedValuesList();
        try {
            Agendamento a = controller.agendar(
                    (Cliente) cbCliente.getSelectedItem(),
                    (Profissional) cbProfissional.getSelectedItem(),
                    data, hora, servicos, txtObservacoes.getText().trim());

            JOptionPane.showMessageDialog(this,
                    String.format("Agendamento #%d criado das %s às %s.%nValor total: R$ %.2f",
                            a.getId(), a.getHorarioInicio().format(FORMATO_HORA),
                            a.getHorarioFim().format(FORMATO_HORA), a.getValorTotal()),
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            listServicos.clearSelection();
            txtObservacoes.setText("");
            atualizarTabela();
        } catch (Exception ex) {
            mostrarErro(ex);
        }
    }

    private void cancelar() {
        Long id = idSelecionado();
        if (id == null) {
            return;
        }
        String motivo = JOptionPane.showInputDialog(this, "Motivo do cancelamento:");
        if (motivo == null) {
            return;
        }
        try {
            controller.cancelarAgendamento(id, motivo);
            atualizarTabela();
        } catch (Exception ex) {
            mostrarErro(ex);
        }
    }

    private void concluir() {
        Long id = idSelecionado();
        if (id == null) {
            return;
        }
        try {
            controller.concluirAgendamento(id);
            JOptionPane.showMessageDialog(this, "Agendamento concluído e comissão calculada!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            atualizarTabela();
        } catch (Exception ex) {
            mostrarErro(ex);
        }
    }

    private Long idSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um agendamento na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return (Long) modeloTabela.getValueAt(linha, 0);
    }

    private void mostrarErro(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
