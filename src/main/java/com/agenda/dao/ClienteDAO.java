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
