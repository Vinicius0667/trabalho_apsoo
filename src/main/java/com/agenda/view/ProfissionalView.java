package com.agenda.view;

import com.agenda.controller.AgendamentoControladora;
import com.agenda.exception.ClienteNaoSelecionadoException;
import com.agenda.exception.DadoInvalidoException;
import com.agenda.exception.DataInvalidaException;
import com.agenda.exception.DataPassadaException;
import com.agenda.exception.ObservacaoInvalidaException;
import com.agenda.exception.ProfissionalNaoSelecionadoException;
import com.agenda.exception.ServicoNaoSelecionadoException;
import com.agenda.model.Agendamento;
import com.agenda.model.Cliente;
import com.agenda.model.Especialidade;
import com.agenda.model.Profissional;
import com.agenda.model.Servico;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicFormattedTextFieldUI;
import javax.swing.plaf.basic.BasicLabelUI;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.StringJoiner;

public class ProfissionalView extends JFrame {
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    private static final Color VINHO = new Color(0x74, 0x16, 0x16);
    private static final Color VINHO_ESCURO = new Color(0x4A, 0x0D, 0x0D);
    private static final Color FUNDO = new Color(0xD9, 0xD9, 0xD9);
    private static final Color CINZA_CAIXA = new Color(0xAD, 0xAD, 0xAD);

    private JComboBox<Cliente> cbCliente;
    private JComboBox<Servico> cbServico;
    private JComboBox<Profissional> cbProfissional;
    private JFormattedTextField txtData;
    private JPanel painelHorarios;
    private JTextArea txtObservacoes;

    private LocalTime horarioSelecionado;
    private JButton botaoHorarioSelecionado;

    private final AgendamentosView agendamentosView;
    private final AgendamentoControladora controller;

    public ProfissionalView(AgendamentosView agendamentosView) {
        super("Agenda - Novo agendamento");
        this.agendamentosView = agendamentosView;
        controller = new AgendamentoControladora();
        inicializarComponentes();
        carregarDados();
        atualizarHorarios();
    }

    private void inicializarComponentes() {
        setSize(480, 720);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel conteudo = new JPanel(new BorderLayout());
        conteudo.setBackground(FUNDO);
        setContentPane(conteudo);

        JLabel titulo = new JLabel("Novo Agendamento", SwingConstants.CENTER);
        titulo.setOpaque(true);
        titulo.setBackground(VINHO);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(titulo.getFont().deriveFont(Font.PLAIN, 24f));
        titulo.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        conteudo.add(titulo, BorderLayout.NORTH);

        cbCliente = criarCombo("Cliente");
        cbServico = criarCombo("Serviço");
        cbProfissional = criarCombo("Profissional");

        txtData = new JFormattedTextField(criarMascaraData());
        txtData.setUI(new BasicFormattedTextFieldUI());
        txtData.setColumns(8);
        txtData.setValue(LocalDate.now().format(FORMATO_DATA));
        txtData.setBackground(VINHO);
        txtData.setForeground(Color.WHITE);
        txtData.setCaretColor(Color.WHITE);
        txtData.setFont(txtData.getFont().deriveFont(15f));
        txtData.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        painelHorarios = new JPanel();
        painelHorarios.setOpaque(false);

        txtObservacoes = new JTextArea(5, 20);
        txtObservacoes.setLineWrap(true);
        txtObservacoes.setWrapStyleWord(true);
        txtObservacoes.setBackground(CINZA_CAIXA);
        txtObservacoes.setForeground(Color.BLACK);
        txtObservacoes.setCaretColor(Color.BLACK);
        txtObservacoes.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        JScrollPane scrollObs = new JScrollPane(txtObservacoes);
        scrollObs.setBorder(BorderFactory.createEmptyBorder());

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        adicionar(form, gbc, cbCliente, 18);
        adicionar(form, gbc, cbServico, 18);
        adicionar(form, gbc, cbProfissional, 14);
        adicionar(form, gbc, criarRotulo("Data"), 4);
        adicionar(form, gbc, alinharEsquerda(txtData), 14);
        adicionar(form, gbc, criarRotulo("Horários disponíveis"), 8);
        adicionar(form, gbc, painelHorarios, 14);
        adicionar(form, gbc, criarRotulo("Observações"), 4);
        adicionar(form, gbc, scrollObs, 0);

        gbc.weighty = 1;
        form.add(Box.createVerticalGlue(), gbc);

        conteudo.add(form, BorderLayout.CENTER);

        JButton btnVoltar = criarBotao("VOLTAR", FUNDO, VINHO);
        btnVoltar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(VINHO, 2), BorderFactory.createEmptyBorder(12, 18, 12, 18)));
        btnVoltar.addActionListener(e -> dispose());

        JButton btnAgendar = criarBotao("SOLICITAR AGENDAMENTO", VINHO, Color.WHITE);
        btnAgendar.setBorder(BorderFactory.createEmptyBorder(14, 22, 14, 22));
        btnAgendar.addActionListener(e -> agendar());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        botoes.setOpaque(false);
        botoes.setBorder(BorderFactory.createEmptyBorder(10, 0, 25, 0));
        botoes.add(btnVoltar);
        botoes.add(btnAgendar);
        conteudo.add(botoes, BorderLayout.SOUTH);

        cbServico.addActionListener(e -> atualizarHorarios());
        cbProfissional.addActionListener(e -> atualizarHorarios());
        txtData.addPropertyChangeListener("value", e -> atualizarHorarios());
    }

    private MaskFormatter criarMascaraData() {
        try {
            MaskFormatter mascara = new MaskFormatter("##/##/####");
            mascara.setPlaceholderCharacter('_');
            return mascara;
        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }
    }

    private void adicionar(JPanel painel, GridBagConstraints gbc, JComponent campo, int espacoAbaixo) {
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(0, 0, espacoAbaixo, 0);
        painel.add(campo, gbc);
    }

    private void carregarDados() {
        try {
            for (Cliente c : controller.listarClientes()) {
                cbCliente.addItem(c);
            }
            for (Servico s : controller.listarServicos()) {
                cbServico.addItem(s);
            }
            
            com.agenda.model.Usuario logado = agendamentosView.getUsuarioLogado();
            Profissional selecionadoParaLogado = null;
            
            for (Profissional p : controller.listarProfissionais()) {
                cbProfissional.addItem(p);
                if (logado != null && p.getId() == logado.getId()) {
                    selecionadoParaLogado = p;
                }
            }
            
            cbCliente.setSelectedIndex(-1);
            cbServico.setSelectedIndex(-1);
            
            if (logado != null && logado.getTipo() != 1) {
                cbProfissional.setSelectedItem(selecionadoParaLogado);
                cbProfissional.setEnabled(false);
            } else {
                cbProfissional.setSelectedIndex(-1);
                cbProfissional.setEnabled(true);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarHorarios() {
        horarioSelecionado = null;
        botaoHorarioSelecionado = null;
        painelHorarios.removeAll();

        Servico servico = (Servico) cbServico.getSelectedItem();
        Profissional profissional = (Profissional) cbProfissional.getSelectedItem();

        if (servico == null || profissional == null) {
            mostrarAvisoHorarios("Escolha o serviço e o profissional para ver os horários.");
        } else {
            try {
                List<LocalTime> horarios = controller.listarHorariosDisponiveis(profissional, servico, txtData.getText());
                if (horarios.isEmpty()) {
                    mostrarAvisoHorarios("Nenhum horário disponível nessa data.");
                } else {
                    painelHorarios.setLayout(new GridLayout(0, 5, 12, 10));
                    for (LocalTime horario : horarios) {
                        painelHorarios.add(criarBotaoHorario(horario));
                    }
                }
            } catch (DadoInvalidoException ex) {
                mostrarAvisoHorarios(ex.getMessage());
            } catch (Exception ex) {
                mostrarAvisoHorarios("Não foi possível carregar os horários.");
            }
        }
        painelHorarios.revalidate();
        painelHorarios.repaint();
    }

    private void mostrarAvisoHorarios(String mensagem) {
        painelHorarios.setLayout(new BorderLayout());
        JLabel aviso = new JLabel("<html>" + mensagem + "</html>");
        aviso.setForeground(Color.DARK_GRAY);
        painelHorarios.add(aviso, BorderLayout.CENTER);
    }

    private JButton criarBotaoHorario(LocalTime horario) {
        JButton botao = criarBotao(horario.format(FORMATO_HORA), VINHO, Color.WHITE);
        botao.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        botao.addActionListener(e -> selecionarHorario(botao, horario));
        return botao;
    }

    private void selecionarHorario(JButton botao, LocalTime horario) {
        if (botaoHorarioSelecionado != null) {
            botaoHorarioSelecionado.setBackground(VINHO);
            botaoHorarioSelecionado.setForeground(Color.WHITE);
        }
        botao.setBackground(Color.WHITE);
        botao.setForeground(VINHO);
        botaoHorarioSelecionado = botao;
        horarioSelecionado = horario;
    }

    private void agendar() {
        try {
            Agendamento a = controller.agendar(
                    (Cliente) cbCliente.getSelectedItem(),
                    (Servico) cbServico.getSelectedItem(),
                    (Profissional) cbProfissional.getSelectedItem(),
                    txtData.getText(),
                    horarioSelecionado,
                    txtObservacoes.getText());

            JOptionPane.showMessageDialog(this,
                    String.format("Agendamento #%d criado para %s em %s das %s às %s.%nValor total: R$ %.2f",
                            a.getId(), a.getCliente().getNome(), a.getDataAgendada().format(FORMATO_DATA),
                            a.getHorarioInicio().format(FORMATO_HORA),
                            a.getHorarioFim().format(FORMATO_HORA), a.getValorTotal()),
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            agendamentosView.atualizarTabela();
            dispose();
        } catch (ClienteNaoSelecionadoException ex) {
            mostrarDadoInvalido(ex, cbCliente);
        } catch (ServicoNaoSelecionadoException ex) {
            mostrarDadoInvalido(ex, cbServico);
        } catch (ProfissionalNaoSelecionadoException ex) {
            mostrarDadoInvalido(ex, cbProfissional);
        } catch (DataInvalidaException | DataPassadaException ex) {
            mostrarDadoInvalido(ex, txtData);
        } catch (ObservacaoInvalidaException ex) {
            mostrarDadoInvalido(ex, txtObservacoes);
        } catch (DadoInvalidoException ex) {
            mostrarDadoInvalido(ex, painelHorarios);
            atualizarHorarios();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarDadoInvalido(DadoInvalidoException ex, JComponent campo) {
        JOptionPane.showMessageDialog(this,
                ex.getMessage() + "\n\nCorrija o campo e tente novamente.",
                "Dado inválido", JOptionPane.ERROR_MESSAGE);
        campo.requestFocusInWindow();
    }

    private JLabel criarRotulo(String texto) {
        JLabel rotulo = new JLabel(texto);
        rotulo.setForeground(Color.BLACK);
        rotulo.setFont(rotulo.getFont().deriveFont(Font.PLAIN, 15f));
        return rotulo;
    }

    private JPanel alinharEsquerda(JComponent campo) {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        painel.setOpaque(false);
        painel.add(campo);
        return painel;
    }

    private JButton criarBotao(String texto, Color fundo, Color letra) {
        JButton botao = new JButton(texto);
        botao.setUI(new BasicButtonUI());
        botao.setBackground(fundo);
        botao.setForeground(letra);
        botao.setFont(botao.getFont().deriveFont(Font.PLAIN, 14f));
        botao.setFocusPainted(false);
        botao.setOpaque(true);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return botao;
    }

    private <T> JComboBox<T> criarCombo(String placeholder) {
        UIManager.put("ComboBox.background", VINHO);
        UIManager.put("ComboBox.foreground", Color.WHITE);
        UIManager.put("ComboBox.selectionBackground", VINHO_ESCURO);
        UIManager.put("ComboBox.selectionForeground", Color.WHITE);
        UIManager.put("ComboBox.buttonBackground", VINHO);
        UIManager.put("ComboBox.buttonShadow", VINHO);
        UIManager.put("ComboBox.buttonDarkShadow", Color.WHITE);
        UIManager.put("ComboBox.buttonHighlight", VINHO);

        JComboBox<T> combo = new JComboBox<>();
        combo.setUI(new BasicComboBoxUI());
        combo.setBackground(VINHO);
        combo.setForeground(Color.WHITE);
        combo.setFont(combo.getFont().deriveFont(Font.PLAIN, 15f));
        combo.setBorder(BorderFactory.createEmptyBorder());
        combo.setPreferredSize(new Dimension(0, 32));
        combo.setFocusable(false);

        DefaultListCellRenderer renderizador = new DefaultListCellRenderer();
        renderizador.setUI(new BasicLabelUI());
        combo.setRenderer((lista, valor, indice, selecionado, foco) -> {
            renderizador.getListCellRendererComponent(lista, textoDoItem(valor, placeholder), indice, selecionado, foco);
            renderizador.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
            return renderizador;
        });
        return combo;
    }

    private String textoDoItem(Object valor, String placeholder) {
        if (valor == null) {
            return placeholder;
        }
        if (valor instanceof Profissional p) {
            StringJoiner especialidades = new StringJoiner(", ", " (", ")");
            especialidades.setEmptyValue("");
            for (Especialidade e : p.getEspecialidades()) {
                especialidades.add(e.getNome());
            }
            return p.getNome() + especialidades;
        }
        return valor.toString();
    }
}
