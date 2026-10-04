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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicLabelUI;
import javax.swing.plaf.basic.BasicTextFieldUI;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.StringJoiner;

public class CadastroAgendamentoView extends TelaComVoltar {
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    private static final Color VINHO = new Color(0x74, 0x16, 0x16);
    private static final Color VINHO_ESCURO = new Color(0x4A, 0x0D, 0x0D);
    private static final Color FUNDO = new Color(0xD9, 0xD9, 0xD9);
    private static final Color CINZA_CAIXA = new Color(0xAD, 0xAD, 0xAD);

    private JComboBox<Cliente> cbCliente;
    private JComboBox<Servico> cbServico;
    private JComboBox<Profissional> cbProfissional;
    private JTextField txtData;
    private JPanel painelHorarios;
    private JTextArea txtObservacoes;

    private LocalTime horarioSelecionado;
    private JButton botaoHorarioSelecionado;

    private final AgendamentoControladora controller;

    public CadastroAgendamentoView(JFrame telaAnterior) {
        super("Agenda - Novo agendamento", telaAnterior);
        controller = new AgendamentoControladora();
        inicializarComponentes();
        carregarDados();
        atualizarHorarios();
    }

    private void inicializarComponentes() {
        setSize(480, 720);
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

        txtData = new JTextField(LocalDate.now().format(FORMATO_DATA), 8);
        txtData.setUI(new BasicTextFieldUI());
        txtData.setBackground(VINHO);
        txtData.setForeground(Color.WHITE);
        txtData.setCaretColor(Color.WHITE);
        txtData.setFont(txtData.getFont().deriveFont(15f));
        txtData.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        txtData.setToolTipText("Formato dd/MM/aaaa");

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

        adicionar(form, gbc, cbCliente, 0, 18);
        adicionar(form, gbc, cbServico, 0, 18);
        adicionar(form, gbc, cbProfissional, 0, 14);
        adicionar(form, gbc, criarRotulo("Data"), 0, 4);
        adicionar(form, gbc, alinharEsquerda(txtData), 0, 14);
        adicionar(form, gbc, criarRotulo("Horários disponíveis"), 0, 8);
        adicionar(form, gbc, painelHorarios, 0, 14);
        adicionar(form, gbc, criarRotulo("Observações"), 0, 4);
        adicionar(form, gbc, scrollObs, 0, 0);

        gbc.weighty = 1;
        form.add(Box.createVerticalGlue(), gbc);

        conteudo.add(form, BorderLayout.CENTER);

        JButton btnVoltar = criarBotao("VOLTAR", FUNDO, VINHO);
        btnVoltar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(VINHO, 2), BorderFactory.createEmptyBorder(12, 18, 12, 18)));
        btnVoltar.addActionListener(e -> voltar());

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
        txtData.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { atualizarHorarios(); }
            @Override public void removeUpdate(DocumentEvent e) { atualizarHorarios(); }
            @Override public void changedUpdate(DocumentEvent e) { atualizarHorarios(); }
        });
    }

    private void adicionar(JPanel painel, GridBagConstraints gbc, JComponent campo, int topo, int baixo) {
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(topo, 0, baixo, 0);
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
            for (Profissional p : controller.listarProfissionais()) {
                cbProfissional.addItem(p);
            }
            cbCliente.setSelectedIndex(-1);
            cbServico.setSelectedIndex(-1);
            cbProfissional.setSelectedIndex(-1);
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
        botao.addActionListener(e -> {
            if (botaoHorarioSelecionado != null) {
                botaoHorarioSelecionado.setBackground(VINHO);
                botaoHorarioSelecionado.setForeground(Color.WHITE);
            }
            botao.setBackground(Color.WHITE);
            botao.setForeground(VINHO);
            botaoHorarioSelecionado = botao;
            horarioSelecionado = horario;
        });
        return botao;
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
            voltar();
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
        if (campo instanceof JTextField texto) {
            texto.selectAll();
        }
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
        JComboBox<T> combo = new JComboBox<>();
        combo.setUI(new ComboVinhoUI());
        combo.setBackground(VINHO);
        combo.setForeground(Color.WHITE);
        combo.setFont(combo.getFont().deriveFont(Font.PLAIN, 15f));
        combo.setBorder(BorderFactory.createEmptyBorder());
        combo.setPreferredSize(new Dimension(0, 32));
        combo.setRenderer(new ItemRenderer(placeholder));
        return combo;
    }

    private static class ComboVinhoUI extends BasicComboBoxUI {
        @Override
        protected JButton createArrowButton() {
            return new SetaButton();
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            hasFocus = false;
            super.paint(g, c);
        }

        @Override
        public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
            super.paintCurrentValue(g, bounds, false);
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            g.setColor(comboBox.getBackground());
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }

    private static class SetaButton extends JButton {
        SetaButton() {
            setBorder(BorderFactory.createEmptyBorder());
            setFocusable(false);
            setPreferredSize(new Dimension(34, 32));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(VINHO);
            g2.fillRect(0, 0, getWidth(), getHeight());
            int cx = getWidth() / 2;
            int cy = getHeight() / 2;
            g2.setColor(Color.WHITE);
            g2.fillPolygon(new int[]{cx - 8, cx + 8, cx}, new int[]{cy - 6, cy - 6, cy + 7}, 3);
            g2.dispose();
        }
    }

    private static class ItemRenderer extends DefaultListCellRenderer {
        private final String placeholder;

        ItemRenderer(String placeholder) {
            this.placeholder = placeholder;
        }

        @Override
        public void updateUI() {
            setUI(new BasicLabelUI());
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
            if (isSelected) {
                setBackground(VINHO_ESCURO);
                setForeground(Color.WHITE);
            }
            if (value == null) {
                setText(placeholder);
            } else if (value instanceof Profissional p) {
                StringJoiner especialidades = new StringJoiner(", ", " (", ")");
                especialidades.setEmptyValue("");
                for (Especialidade e : p.getEspecialidades()) {
                    especialidades.add(e.getNome());
                }
                setText(p.getNome() + especialidades);
            }
            return this;
        }
    }
}
