package com.agenda.controller;

import com.agenda.dao.AgendaProfissionalDAO;
import com.agenda.dao.AgendamentoDAO;
import com.agenda.dao.ClienteDAO;
import com.agenda.dao.RepasseComissaoDAO;
import com.agenda.dao.ServicoDAO;
import com.agenda.dao.UsuarioDAO;
import com.agenda.model.Agendamento;
import com.agenda.model.Cliente;
import com.agenda.model.ItemAgendamento;
import com.agenda.model.Profissional;
import com.agenda.model.RepasseComissao;
import com.agenda.model.Servico;
import com.agenda.model.StatusAgendamento;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class ProfissionalControladora {
    private final AgendamentoDAO agendamentoDAO;
    private final ClienteDAO clienteDAO;
    private final ServicoDAO servicoDAO;
    private final UsuarioDAO usuarioDAO;
    private final RepasseComissaoDAO repasseDAO;
    private final AgendaProfissionalDAO agendaDAO;

    public ProfissionalControladora() {
        this.agendamentoDAO = new AgendamentoDAO();
        this.clienteDAO = new ClienteDAO();
        this.servicoDAO = new ServicoDAO();
        this.usuarioDAO = new UsuarioDAO();
        this.repasseDAO = new RepasseComissaoDAO();
        this.agendaDAO = new AgendaProfissionalDAO();
    }

    public Agendamento agendar(Cliente cliente, Profissional profissional, LocalDate data, LocalTime horarioInicio,
                               List<Servico> servicos, String observacoes) throws Exception {
        if (cliente == null || profissional == null) {
            throw new Exception("Selecione o cliente e o profissional.");
        }
        if (servicos == null || servicos.isEmpty()) {
            throw new Exception("Selecione pelo menos um serviço.");
        }
        if (data.isBefore(LocalDate.now())) {
            throw new Exception("Não é possível agendar em uma data passada.");
        }

        Agendamento agendamento = new Agendamento();
        agendamento.setCliente(cliente);
        agendamento.setProfissional(profissional);
        agendamento.setDataAgendada(data);
        agendamento.setHorarioInicio(horarioInicio);
        agendamento.setObservacoes(observacoes);
        for (Servico servico : servicos) {
            agendamento.adicionarItem(new ItemAgendamento(servico));
        }
        agendamento.setHorarioFim(horarioInicio.plusMinutes(agendamento.getDuracaoTotal()));

        try {
            if (agendamentoDAO.existeConflito(profissional.getIdProfissional(), data,
                    agendamento.getHorarioInicio(), agendamento.getHorarioFim())) {
                throw new Exception("O profissional já possui um agendamento nesse horário.");
            }
            agendamentoDAO.inserir(agendamento);
        } catch (SQLException e) {
            throw new Exception("Erro ao salvar agendamento: " + e.getMessage(), e);
        }
        return agendamento;
    }

    public void cancelarAgendamento(long idAgendamento, String motivo) throws Exception {
        if (motivo == null || motivo.isBlank()) {
            throw new Exception("Informe o motivo do cancelamento.");
        }
        Agendamento agendamento = buscarAgendamentoAtivo(idAgendamento);
        try {
            agendamentoDAO.cancelar(agendamento.getId(), motivo);
        } catch (SQLException e) {
            throw new Exception("Erro ao cancelar agendamento: " + e.getMessage(), e);
        }
    }

    public void concluirAgendamento(long idAgendamento) throws Exception {
        Agendamento agendamento = buscarAgendamentoAtivo(idAgendamento);
        try {
            agendamento.setStatus(StatusAgendamento.CONCLUIDO);
            agendamento.setDataRealizacao(LocalDate.now());
            agendamento.setHorarioInicioRealizacao(agendamento.getHorarioInicio());
            agendamento.setHorarioFimRealizacao(agendamento.getHorarioFim());
            agendamentoDAO.registrarRealizacao(agendamento);

            Profissional profissional = usuarioDAO.buscarProfissionalPorId(agendamento.getProfissional().getIdProfissional());
            RepasseComissao repasse = new RepasseComissao();
            repasse.setValorPagoCliente(agendamento.getValorTotal());
            repasse.setPercentualAplicado((float) profissional.getPercentualComissao());
            repasse.setValorComissao(agendamento.getValorTotal() * profissional.getPercentualComissao() / 100.0);
            repasse.setDataCalculo(LocalDateTime.now());
            repasse.setStatusPagamento("PENDENTE");
            repasseDAO.inserir(agendamento.getId(), repasse);
            agendamento.setRepasseComissao(repasse);
        } catch (SQLException e) {
            throw new Exception("Erro ao concluir agendamento: " + e.getMessage(), e);
        }
    }

    public List<Agendamento> listarAgendamentos(Profissional profissional) throws Exception {
        try {
            profissional.setAgendamentos(agendamentoDAO.listarPorProfissional(profissional.getIdProfissional()));
            return profissional.getAgendamentos();
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

    public List<Servico> listarServicos() throws Exception {
        try {
            return servicoDAO.listar();
        } catch (SQLException e) {
            throw new Exception("Erro ao listar serviços: " + e.getMessage(), e);
        }
    }

    public List<Profissional> listarProfissionais() throws Exception {
        try {
            List<Profissional> profissionais = usuarioDAO.listarProfissionaisAtivos();
            for (Profissional p : profissionais) {
                p.setAgendas(agendaDAO.listarPorProfissional(p.getIdProfissional()));
            }
            return profissionais;
        } catch (SQLException e) {
            throw new Exception("Erro ao listar profissionais: " + e.getMessage(), e);
        }
    }

    private Agendamento buscarAgendamentoAtivo(long idAgendamento) throws Exception {
        Agendamento agendamento;
        try {
            agendamento = agendamentoDAO.buscarPorId(idAgendamento);
        } catch (SQLException e) {
            throw new Exception("Erro ao buscar agendamento: " + e.getMessage(), e);
        }
        if (agendamento == null) {
            throw new Exception("Agendamento não encontrado.");
        }
        if (agendamento.getStatus() == StatusAgendamento.CANCELADO
                || agendamento.getStatus() == StatusAgendamento.CONCLUIDO) {
            throw new Exception("Agendamento já está " + agendamento.getStatus() + ".");
        }
        return agendamento;
    }
}
