package com.agenda.dao;

import com.agenda.model.Servico;
import com.agenda.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServicoDAO {
    public List<Servico> listar() throws SQLException {
        List<Servico> lista = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM servico ORDER BY nome");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(montarServico(rs));
            }
        }
        return lista;
    }

    public Servico buscarPorId(long id) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM servico WHERE id = ?")) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? montarServico(rs) : null;
            }
        }
    }

    private Servico montarServico(ResultSet rs) throws SQLException {
        Servico s = new Servico();
        s.setId(rs.getLong("id"));
        s.setNome(rs.getString("nome"));
        s.setDescricao(rs.getString("descricao"));
        s.setDuracaoMinutos(rs.getInt("duracao_minutos"));
        s.setValorPadrao(rs.getDouble("valor_padrao"));
        return s;
    }
}
