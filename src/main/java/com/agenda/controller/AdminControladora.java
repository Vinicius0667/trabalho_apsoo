package com.agenda.controller;

import com.agenda.dao.AgendamentoDAO;
import com.agenda.dao.UsuarioDAO;
import com.agenda.model.Administrador;
import com.agenda.model.Agendamento;

import java.sql.SQLException;
import java.util.List;

public class AdminControladora {
    private static final long ID_ADMIN_PADRAO = 1;

    private final AgendamentoDAO agendamentoDAO;
    private final UsuarioDAO usuarioDAO;
    private final ProfissionalControladora profissionalControladora;

    private Administrador administrador;

    public AdminControladora() {
        this.agendamentoDAO = new AgendamentoDAO();
        this.usuarioDAO = new UsuarioDAO();
        this.profissionalControladora = new ProfissionalControladora();
    }

    public Administrador getAdministrador() throws Exception {
        if (administrador == null) {
            try {
                administrador = usuarioDAO.buscarAdministradorPorId(ID_ADMIN_PADRAO);
            } catch (SQLException e) {
                throw new Exception("Erro ao buscar administrador: " + e.getMessage(), e);
            }
        }
        return administrador;
    }

    public List<Agendamento> listarTodosAgendamentos() throws Exception {
        try {
            return agendamentoDAO.listar();
        } catch (SQLException e) {
            throw new Exception("Erro ao listar agendamentos: " + e.getMessage(), e);
        }
    }

    public void cancelarAgendamento(long idAgendamento, String motivo) throws Exception {
        profissionalControladora.cancelarAgendamento(idAgendamento, motivo);
    }
}
