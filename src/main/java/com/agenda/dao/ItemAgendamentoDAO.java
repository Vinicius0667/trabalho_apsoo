package com.agenda.dao;

import com.agenda.model.ItemAgendamento;
import com.agenda.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemAgendamentoDAO {
    private final ServicoDAO servicoDAO = new ServicoDAO();

    public void inserir(Connection conn, long idAgendamento, ItemAgendamento item) throws SQLException {
        String sql = "INSERT INTO item_agendamento (id_agendamento, id_servico, duracao_realizada, valor_cobrado) " +
                     "VALUES (?, ?, ?, ?) RETURNING id";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idAgendamento);
            stmt.setLong(2, item.getServico().getId());
            stmt.setInt(3, item.getDuracaoRealizada());
            stmt.setFloat(4, item.getValorCobrado());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    item.setId(rs.getLong("id"));
                }
            }
        }
    }

    public List<ItemAgendamento> listarPorAgendamento(long idAgendamento) throws SQLException {
        List<ItemAgendamento> lista = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM item_agendamento WHERE id_agendamento = ?")) {
            stmt.setLong(1, idAgendamento);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ItemAgendamento item = new ItemAgendamento();
                    item.setId(rs.getLong("id"));
                    item.setDuracaoRealizada(rs.getInt("duracao_realizada"));
                    item.setValorCobrado(rs.getFloat("valor_cobrado"));
                    item.setServico(servicoDAO.buscarPorId(rs.getLong("id_servico")));
                    lista.add(item);
                }
            }
        }
        return lista;
    }
}
