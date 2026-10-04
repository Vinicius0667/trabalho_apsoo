package com.agenda.controller;

import com.agenda.dao.AgendamentoDAO;
import com.agenda.dao.ClienteDAO;
import com.agenda.dao.ServicoDAO;
import com.agenda.dao.UsuarioDAO;
import com.agenda.exception.ClienteNaoSelecionadoException;
import com.agenda.exception.DadoInvalidoException;
import com.agenda.exception.DataInvalidaException;
import com.agenda.exception.DataPassadaException;
import com.agenda.exception.HorarioIndisponivelException;
import com.agenda.exception.HorarioInvalidoException;
import com.agenda.exception.HorarioPassadoException;
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
import java.util.ArrayList;
import java.util.List;

public class AgendamentoControladora {
    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    private static final List<LocalTime> HORARIOS_ATENDIMENTO = List.of(
            LocalTime.of(8, 0), LocalTime.of(9, 0), LocalTime.of(10, 0), LocalTime.of(11, 0),
            LocalTime.of(13, 0), LocalTime.of(14, 0), LocalTime.of(15, 0), LocalTime.of(16, 0),
            LocalTime.of(17, 0));

    private static final int TAMANHO_MAXIMO_OBSERVACAO = 255;

    private final AgendamentoDAO agendamentoDAO;
    private final ClienteDAO clienteDAO;
    private final ServicoDAO servicoDAO;
    private final UsuarioDAO usuarioDAO;

    public AgendamentoControladora() {
        this.agendamentoDAO = new AgendamentoDAO();
        this.clienteDAO = new ClienteDAO();
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

    public List<Cliente> listarClientes() throws Exception {
        try {
            return clienteDAO.listar();
        } catch (SQLException e) {
            throw new Exception("Erro ao listar clientes: " + e.getMessage(), e);
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

    public List<LocalTime> listarHorariosDisponiveis(Profissional profissional, Servico servico, String textoData)
            throws DadoInvalidoException, Exception {
        List<LocalTime> disponiveis = new ArrayList<>();
        if (profissional == null || servico == null) {
            return disponiveis;
        }
        LocalDate data = validarData(textoData);

        try {
            for (LocalTime inicio : HORARIOS_ATENDIMENTO) {
                boolean jaPassou = data.isEqual(LocalDate.now()) && inicio.isBefore(LocalTime.now());
                LocalTime fim = inicio.plusMinutes(servico.getDuracaoMinutos());
                if (!jaPassou && !agendamentoDAO.existeConflito(profissional.getIdProfissional(), data, inicio, fim)) {
                    disponiveis.add(inicio);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Erro ao consultar horários: " + e.getMessage(), e);
        }
        return disponiveis;
    }

    public Agendamento agendar(Cliente cliente, Servico servico, Profissional profissional, String textoData,
                               LocalTime horarioInicio, String observacoes) throws DadoInvalidoException, Exception {
        if (cliente == null) {
            throw new ClienteNaoSelecionadoException("Selecione o cliente do agendamento.");
        }
        if (servico == null) {
            throw new ServicoNaoSelecionadoException("Selecione o serviço.");
        }
        if (profissional == null) {
            throw new ProfissionalNaoSelecionadoException("Selecione o profissional que fará o atendimento.");
        }

        LocalDate data = validarData(textoData);
        if (horarioInicio == null) {
            throw new HorarioInvalidoException("Selecione um dos horários disponíveis.");
        }
        if (data.isEqual(LocalDate.now()) && horarioInicio.isBefore(LocalTime.now())) {
            throw new HorarioPassadoException("Esse horário de hoje já passou. Escolha um horário mais tarde.");
        }

        String obs = observacoes == null ? "" : observacoes.trim();
        if (obs.length() > TAMANHO_MAXIMO_OBSERVACAO) {
            throw new ObservacaoInvalidaException(
                    "A observação pode ter no máximo " + TAMANHO_MAXIMO_OBSERVACAO + " caracteres "
                    + "(atualmente tem " + obs.length() + ").");
        }

        Agendamento agendamento = new Agendamento(cliente, 
            profissional, 
            data, 
            horarioInicio, 
            obs.isEmpty() ? null : obs, 
            new ItemAgendamento(servico));
            
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
}
