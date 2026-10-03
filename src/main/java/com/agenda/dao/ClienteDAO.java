package com.agenda.dao;

import com.agenda.model.Cliente;
import com.agenda.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    // Recebe a conexão para participar da mesma transação do AgendamentoDAO
    public void inserir(Connection conn, Cliente cliente) throws SQLException {
        String sql = "INSERT INTO cliente (nome, observacoes) VALUES (?, ?) RETURNING id, data_cadastro";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getObservacoes());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    cliente.setId(rs.getLong("id"));
                    cliente.setDataCadastro(rs.getObject("data_cadastro", LocalDate.class));
                }
            }
        }
    }

    public List<Cliente> listar() throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM cliente ORDER BY nome");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Cliente c = new Cliente();
                c.setId(rs.getLong("id"));
                c.setNome(rs.getString("nome"));
                c.setDataCadastro(rs.getObject("data_cadastro", LocalDate.class));
                c.setObservacoes(rs.getString("observacoes"));
                lista.add(c);
            }
        }
        return lista;
    }
}
