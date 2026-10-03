package com.agenda.controller;

import com.agenda.dao.AgendamentoDAO;
import com.agenda.dao.ServicoDAO;
import com.agenda.dao.UsuarioDAO;
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
import com.agenda.model.Cliente;
import com.agenda.model.ItemAgendamento;
import com.agenda.model.Profissional;
import com.agenda.model.Servico;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;

/** Regras da tela inicial (lista de agendamentos) e do cadastro de agendamento. */
public class AgendamentoControladora {

    // STRICT faz datas inexistentes como 31/02 serem rejeitadas em vez de "ajustadas"
    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter FORMATO_HORA =
            DateTimeFormatter.ofPattern("HH:mm").withResolverStyle(ResolverStyle.STRICT);

    // Tamanhos das colunas no init.sql
    private static final int TAMANHO_MAXIMO_NOME = 60;
    private static final int TAMANHO_MAXIMO_OBSERVACAO = 255;

    private final AgendamentoDAO agendamentoDAO;
    private final ServicoDAO servicoDAO;
    private final UsuarioDAO usuarioDAO;

    public AgendamentoControladora() {
        this.agendamentoDAO = new AgendamentoDAO();
        this.servicoDAO = new ServicoDAO();
        this.usuarioDAO = new UsuarioDAO();
    }

    public List<Agendamento> listarTodosAgendamentos() throws Exception {
        try {
            return agendamentoDAO.listar();
        } catch (SQLException e) {
            throw new Exception("Erro ao listar agendamentos: " + e.getMessage(), e);
        }
    }

    public List<Profissional> listarProfissionais() throws Exception {
        try {
            return usuarioDAO.listarProfissionaisAtivos();
        } catch (SQLException e) {
            throw new Exception("Erro ao listar profissionais: " + e.getMessage(), e);
        }
    }

    public List<Servico> listarServicos() throws Exception {
        try {
            return servicoDAO.listar();
        } catch (SQLException e) {
            throw new Exception("Erro ao listar serviços: " + e.getMessage(), e);
        }
    }

    /**
     * Valida os dados digitados, cria o cliente e salva o agendamento.
     * Cada dado incorreto lança a sua própria exceção (subclasses de DadoInvalidoException).
     */
    public Agendamento agendar(String nomeCliente, Profissional profissional, String textoData, String textoHora,
                               List<Servico> servicos, String observacoes) throws DadoInvalidoException, Exception {
        Cliente cliente = new Cliente();
        cliente.setNome(validarNomeCliente(nomeCliente));

        if (profissional == null) {
            throw new ProfissionalNaoSelecionadoException("Selecione o profissional que fará o atendimento.");
        }

        LocalDate data = validarData(textoData);
        LocalTime horarioInicio = validarHorario(textoHora, data);

        if (servicos == null || servicos.isEmpty()) {
            throw new ServicoNaoSelecionadoException("Selecione pelo menos um serviço.");
        }

        String obs = observacoes == null ? "" : observacoes.trim();
        if (obs.length() > TAMANHO_MAXIMO_OBSERVACAO) {
            throw new ObservacaoInvalidaException(
                    "A observação pode ter no máximo " + TAMANHO_MAXIMO_OBSERVACAO + " caracteres "
                    + "(atualmente tem " + obs.length() + ").");
        }

        Agendamento agendamento = new Agendamento();
        agendamento.setCliente(cliente);
        agendamento.setProfissional(profissional);
        agendamento.setDataAgendada(data);
        agendamento.setHorarioInicio(horarioInicio);
        agendamento.setObservacoes(obs.isEmpty() ? null : obs);
        for (Servico servico : servicos) {
            agendamento.adicionarItem(new ItemAgendamento(servico));
        }

        // Comparando em minutos do dia evita que um horário depois da meia-noite "dê a volta"
        int fimEmMinutos = horarioInicio.toSecondOfDay() / 60 + agendamento.getDuracaoTotal();
        if (fimEmMinutos >= 24 * 60) {
            throw new HorarioInvalidoException(
                    "Os serviços escolhidos duram " + agendamento.getDuracaoTotal() + " minutos e "
                    + "terminariam depois da meia-noite. Escolha um horário mais cedo.");
        }
        agendamento.setHorarioFim(horarioInicio.plusMinutes(agendamento.getDuracaoTotal()));

        try {
            if (agendamentoDAO.existeConflito(profissional.getIdProfissional(), data,
                    agendamento.getHorarioInicio(), agendamento.getHorarioFim())) {
                throw new HorarioIndisponivelException(
                        profissional.getNome() + " já possui um agendamento entre "
                        + agendamento.getHorarioInicio().format(FORMATO_HORA) + " e "
                        + agendamento.getHorarioFim().format(FORMATO_HORA) + ". Escolha outro horário.");
            }
            agendamentoDAO.inserir(agendamento);
        } catch (SQLException e) {
            throw new Exception("Erro ao salvar agendamento: " + e.getMessage(), e);
        }
        return agendamento;
    }

    private String validarNomeCliente(String nome) throws NomeClienteInvalidoException {
        String valor = nome == null ? "" : nome.trim().replaceAll("\\s+", " ");
        if (valor.isEmpty()) {
            throw new NomeClienteInvalidoException("O nome do cliente não pode ficar vazio.");
        }
        if (valor.length() < 3) {
            throw new NomeClienteInvalidoException("O nome do cliente deve ter pelo menos 3 letras.");
        }
        if (valor.length() > TAMANHO_MAXIMO_NOME) {
            throw new NomeClienteInvalidoException(
                    "O nome do cliente pode ter no máximo " + TAMANHO_MAXIMO_NOME + " caracteres.");
        }
        // \p{L} aceita letras com acento (ç, ã, é...)
        if (!valor.matches("[\\p{L} '-]+")) {
            throw new NomeClienteInvalidoException("O nome do cliente deve conter apenas letras.");
        }
        return valor;
    }

    private LocalDate validarData(String texto) throws DadoInvalidoException {
        LocalDate data;
        try {
            data = LocalDate.parse(texto == null ? "" : texto.trim(), FORMATO_DATA);
        } catch (DateTimeParseException e) {
            throw new DataInvalidaException(
                    "Data inválida: \"" + texto + "\". Use o formato dd/MM/aaaa com uma data que exista (ex.: 25/12/2026).");
        }
        if (data.isBefore(LocalDate.now())) {
            throw new DataPassadaException("Não é possível agendar em uma data que já passou.");
        }
        return data;
    }

    private LocalTime validarHorario(String texto, LocalDate data) throws DadoInvalidoException {
        LocalTime horario;
        try {
            horario = LocalTime.parse(texto == null ? "" : texto.trim(), FORMATO_HORA);
        } catch (DateTimeParseException e) {
            throw new HorarioInvalidoException(
                    "Horário inválido: \"" + texto + "\". Use o formato HH:mm (ex.: 09:30).");
        }
        if (data.isEqual(LocalDate.now()) && horario.isBefore(LocalTime.now())) {
            throw new HorarioPassadoException("Esse horário de hoje já passou. Escolha um horário mais tarde.");
        }
        return horario;
    }
}
