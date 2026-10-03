package com.agenda.dao;

import com.agenda.model.AgendaProfissional;
import com.agenda.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AgendaProfissionalDAO {

    public List<AgendaProfissional> listarPorProfissional(long idProfissional) throws SQLException {
        List<AgendaProfissional> lista = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM agenda_profissional WHERE id_profissional = ? ORDER BY data, horario_inicio")) {
            stmt.setLong(1, idProfissional);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AgendaProfissional a = new AgendaProfissional();
                    a.setIdAgenda(rs.getLong("id_agenda"));
                    a.setData(rs.getObject("data", LocalDate.class));
                    a.setHorarioInicio(rs.getObject("horario_inicio", LocalTime.class));
                    a.setHorarioFim(rs.getObject("horario_fim", LocalTime.class));
                    a.setStatus(rs.getString("status"));
                    lista.add(a);
                }
            }
        }
        return lista;
    }
}
