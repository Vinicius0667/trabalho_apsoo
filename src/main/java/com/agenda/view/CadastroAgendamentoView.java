package com.agenda.view;

import com.agenda.controller.AgendamentoControladora;
import com.agenda.exception.DadoInvalidoException;
import com.agenda.exception.DataInvalidaException;
import com.agenda.exception.DataPassadaException;
import com.agenda.exception.HorarioIndisponivelException;
import com.agenda.exception.HorarioInvalidoException;
import com.agenda.exception.HorarioPassadoException;
import com.agenda.exception.NomeClienteInvalidoException;
import com.agenda.exception.ObservacaoInvalidaException;
import com.agenda.exception.ProfissionalNaoSelecionadoException;
import com.agenda.exception.ServicoNaoSelecionadoException;
import com.agenda.model.Agendamento;
import com.agenda.model.Especialidade;
import com.agenda.model.Profissional;
import com.agenda.model.Servico;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.StringJoiner;

/** Cadastro de um novo agendamento. O cliente é criado junto com o agendamento. */
public class CadastroAgendamentoView extends TelaComVoltar {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    private JTextField txtNomeCliente;
    private JComboBox<Profissional> cbProfissional;
    private JTextField txtData;
    private JTextField txtHora;
    private JList<Servico> listServicos;
    private JTextField txtObservacoes;

    private final AgendamentoControladora controller;

    public CadastroAgendamentoView(JFrame telaAnterior) {
        super("Agenda - Novo agendamento", telaAnterior);
        controller = new AgendamentoControladora();
        inicializarComponentes();
        carregarDados();
    }

    private void inicializarComponentes() {
        setSize(600, 480);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Novo agendamento"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNomeCliente = new JTextField(25);
        cbProfissional = new JComboBox<>();
        cbProfissional.setRenderer(new ProfissionalRenderer());
        txtData = new JTextField(LocalDate.now().format(FORMATO_DATA), 10);
        txtHora = new JTextField(5);
        listServicos = new JList<>();
        listServicos.setVisibleRowCount(5);
        txtObservacoes = new JTextField(25);

        adicionarCampo(form, gbc, 0, "Nome do cliente:", txtNomeCliente);
        adicionarCampo(form, gbc, 1, "Profissional:", cbProfissional);
        adicionarCampo(form, gbc, 2, "Data (dd/MM/aaaa):", txtData);
        adicionarCampo(form, gbc, 3, "Hora (HH:mm):", txtHora);
        adicionarCampo(form, gbc, 4, "Serviços (Ctrl p/ vários):", new JScrollPane(listServicos));
        adicionarCampo(form, gbc, 5, "Observações:", txtObservacoes);

        add(form, BorderLayout.CENTER);

        JPanel botoes = new JPanel(new BorderLayout());
        JPanel esquerda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        esquerda.add(criarBotaoVoltar());
        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAgendar = new JButton("Agendar");
        direita.add(btnAgendar);
        botoes.add(esquerda, BorderLayout.WEST);
        botoes.add(direita, BorderLayout.EAST);
        add(botoes, BorderLayout.SOUTH);

        btnAgendar.addActionListener(e -> agendar());
        getRootPane().setDefaultButton(btnAgendar);
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
            cbProfissional.setSelectedIndex(-1);
            listServicos.setListData(controller.listarServicos().toArray(new Servico[0]));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agendar() {
        try {
            Agendamento a = controller.agendar(
                    txtNomeCliente.getText(),
                    (Profissional) cbProfissional.getSelectedItem(),
                    txtData.getText(),
                    txtHora.getText(),
                    listServicos.getSelectedValuesList(),
                    txtObservacoes.getText());

            JOptionPane.showMessageDialog(this,
                    String.format("Agendamento #%d criado para %s das %s às %s.%nValor total: R$ %.2f",
                            a.getId(), a.getCliente().getNome(),
                            a.getHorarioInicio().format(FORMATO_HORA),
                            a.getHorarioFim().format(FORMATO_HORA), a.getValorTotal()),
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            voltar();
        } catch (NomeClienteInvalidoException ex) {
            mostrarDadoInvalido(ex, txtNomeCliente);
        } catch (ProfissionalNaoSelecionadoException ex) {
            mostrarDadoInvalido(ex, cbProfissional);
        } catch (DataInvalidaException | DataPassadaException ex) {
            mostrarDadoInvalido(ex, txtData);
        } catch (HorarioInvalidoException | HorarioPassadoException | HorarioIndisponivelException ex) {
            mostrarDadoInvalido(ex, txtHora);
        } catch (ServicoNaoSelecionadoException ex) {
            mostrarDadoInvalido(ex, listServicos);
        } catch (ObservacaoInvalidaException ex) {
            mostrarDadoInvalido(ex, txtObservacoes);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Janela de erro com OK; depois coloca o cursor no campo para a pessoa reescrever. */
    private void mostrarDadoInvalido(DadoInvalidoException ex, JComponent campo) {
        JOptionPane.showMessageDialog(this,
                ex.getMessage() + "\n\nCorrija o campo e tente novamente.",
                "Dado inválido", JOptionPane.ERROR_MESSAGE);
        if (campo instanceof JTextField texto) {
            texto.selectAll();
        }
        campo.requestFocusInWindow();
    }

    /** Mostra o profissional com as especialidades, ex.: "Maria Souza (Cabelo, Unhas)". */
    private static class ProfissionalRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Profissional p) {
                StringJoiner especialidades = new StringJoiner(", ", " (", ")");
                especialidades.setEmptyValue("");
                for (Especialidade e : p.getEspecialidades()) {
                    especialidades.add(e.getNome());
                }
                setText(p.getNome() + especialidades);
            } else if (value == null) {
                setText("Selecione...");
            }
            return this;
        }
    }
}
