package com.agenda.dao;

import com.agenda.model.Agendamento;
import com.agenda.model.Cliente;
import com.agenda.model.ItemAgendamento;
import com.agenda.model.Profissional;
import com.agenda.model.StatusAgendamento;
import com.agenda.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AgendamentoDAO {
    private static final String SQL_SELECT =
            "SELECT a.*, c.nome AS nome_cliente, u.nome AS nome_profissional " +
            "FROM agendamento a " +
            "JOIN cliente c ON c.id = a.id_cliente " +
            "JOIN usuario u ON u.id = a.id_profissional ";

    private final ItemAgendamentoDAO itemDAO = new ItemAgendamentoDAO();

    public void inserir(Agendamento agendamento) throws SQLException {
        String sql = "INSERT INTO agendamento " +
                     "(id_cliente, id_profissional, data_agendada, horario_inicio, horario_fim, status, observacoes) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";

        Connection conn = DatabaseConnection.getInstance().getConnection();
        try {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, agendamento.getCliente().getId());
                stmt.setLong(2, agendamento.getProfissional().getIdProfissional());
                stmt.setObject(3, agendamento.getDataAgendada());
                stmt.setObject(4, agendamento.getHorarioInicio());
                stmt.setObject(5, agendamento.getHorarioFim());
                stmt.setInt(6, agendamento.getStatus().getCodigo());
                stmt.setString(7, agendamento.getObservacoes());

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        agendamento.setId(rs.getLong("id"));
                    }
                }
            }

            for (ItemAgendamento item : agendamento.getItens()) {
                itemDAO.inserir(conn, agendamento.getId(), item);
            }

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public boolean existeConflito(long idProfissional, LocalDate data, LocalTime inicio, LocalTime fim) throws SQLException {
        String sql = "SELECT 1 FROM agendamento " +
                     "WHERE id_profissional = ? AND data_agendada = ? AND status NOT IN (?, ?) " +
                     "AND horario_inicio < ? AND horario_fim > ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idProfissional);
            stmt.setObject(2, data);
            stmt.setInt(3, StatusAgendamento.CANCELADO.getCodigo());
            stmt.setInt(4, StatusAgendamento.CONCLUIDO.getCodigo());
            stmt.setObject(5, fim);
            stmt.setObject(6, inicio);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<Agendamento> listar() throws SQLException {
        List<Agendamento> lista = consultar(SQL_SELECT + "ORDER BY a.data_agendada, a.horario_inicio", null);
        for (Agendamento agendamento : lista) {
            agendamento.setItens(itemDAO.listarPorAgendamento(agendamento.getId()));
        }
        return lista;
    }

    public List<Agendamento> listarPorProfissional(long idProfissional) throws SQLException {
        return consultar(SQL_SELECT + "WHERE a.id_profissional = ? ORDER BY a.data_agendada, a.horario_inicio", idProfissional);
    }

    public Agendamento buscarPorId(long id) throws SQLException {
        List<Agendamento> lista = consultar(SQL_SELECT + "WHERE a.id = ?", id);
        if (lista.isEmpty()) {
            return null;
        }
        Agendamento agendamento = lista.get(0);
        agendamento.setItens(itemDAO.listarPorAgendamento(id));
        return agendamento;
    }

    public void cancelar(long id, String motivo) throws SQLException {
        String sql = "UPDATE agendamento SET status = ?, motivo_cancelamento = ? WHERE id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, StatusAgendamento.CANCELADO.getCodigo());
            stmt.setString(2, motivo);
            stmt.setLong(3, id);
            stmt.executeUpdate();
        }
    }

    public void registrarRealizacao(Agendamento agendamento) throws SQLException {
        String sql = "UPDATE agendamento SET status = ?, data_realizacao = ?, " +
                     "horario_inicio_realizacao = ?, horario_fim_realizacao = ? WHERE id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, agendamento.getStatus().getCodigo());
            stmt.setObject(2, agendamento.getDataRealizacao());
            stmt.setObject(3, agendamento.getHorarioInicioRealizacao());
            stmt.setObject(4, agendamento.getHorarioFimRealizacao());
            stmt.setLong(5, agendamento.getId());
            stmt.executeUpdate();
        }
    }

    private List<Agendamento> consultar(String sql, Long parametro) throws SQLException {
        List<Agendamento> lista = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (parametro != null) {
                stmt.setLong(1, parametro);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(montarAgendamento(rs));
                }
            }
        }
        return lista;
    }

    private Agendamento montarAgendamento(ResultSet rs) throws SQLException {
        Agendamento a = new Agendamento();
        a.setId(rs.getLong("id"));
        a.setDataAgendada(rs.getObject("data_agendada", LocalDate.class));
        a.setHorarioInicio(rs.getObject("horario_inicio", LocalTime.class));
        a.setHorarioFim(rs.getObject("horario_fim", LocalTime.class));
        a.setStatus(StatusAgendamento.fromCodigo(rs.getInt("status")));
        a.setMotivoCancelamento(rs.getString("motivo_cancelamento"));
        a.setObservacoes(rs.getString("observacoes"));
        a.setDataRealizacao(rs.getObject("data_realizacao", LocalDate.class));
        a.setHorarioInicioRealizacao(rs.getObject("horario_inicio_realizacao", LocalTime.class));
        a.setHorarioFimRealizacao(rs.getObject("horario_fim_realizacao", LocalTime.class));

        Cliente cliente = new Cliente();
        cliente.setId(rs.getLong("id_cliente"));
        cliente.setNome(rs.getString("nome_cliente"));
        a.setCliente(cliente);

        Profissional profissional = new Profissional();
        profissional.setId(rs.getLong("id_profissional"));
        profissional.setIdProfissional(rs.getLong("id_profissional"));
        profissional.setNome(rs.getString("nome_profissional"));
        a.setProfissional(profissional);

        return a;
    }
}
