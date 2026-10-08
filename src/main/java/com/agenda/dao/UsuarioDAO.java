package com.agenda.dao;

import com.agenda.model.Administrador;
import com.agenda.model.Especialidade;
import com.agenda.model.Profissional;
import com.agenda.model.Usuario;
import com.agenda.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {
    private static final String SQL_PROFISSIONAL =
            "SELECT u.id, u.nome, u.cpf, u.telefone, u.email, u.tipo, " +
            "       p.percentual_comissao, p.status_profissional, p.ativo " +
            "FROM profissional p JOIN usuario u ON u.id = p.id_profissional ";

    public List<Profissional> listarProfissionaisAtivos() throws SQLException {
        List<Profissional> lista = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(SQL_PROFISSIONAL + "WHERE p.ativo ORDER BY u.nome");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(montarProfissional(rs));
            }
        }
        for (Profissional p : lista) {
            p.setEspecialidades(listarEspecialidades(p.getIdProfissional()));
        }
        return lista;
    }

    public Profissional buscarProfissionalPorId(long id) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        Profissional profissional = null;

        try (PreparedStatement stmt = conn.prepareStatement(SQL_PROFISSIONAL + "WHERE p.id_profissional = ?")) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    profissional = montarProfissional(rs);
                }
            }
        }
        if (profissional != null) {
            profissional.setEspecialidades(listarEspecialidades(id));
        }
        return profissional;
    }

    public Usuario autenticarUsuario(String nome, String senhaMD5) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String sql = "SELECT id, tipo FROM usuario WHERE nome = ? AND senha = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome);
            stmt.setString(2, senhaMD5);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int tipo = rs.getInt("tipo");
                    long id = rs.getLong("id");
                    if (tipo == 1) {
                        return buscarAdministradorPorId(id);
                    } else {
                        Profissional p = buscarProfissionalPorId(id);
                        if (p != null && p.isAtivo()) {
                            return p;
                        }
                    }
                }
            }
        }
        return null;
    }

    public Administrador buscarAdministradorPorId(long id) throws SQLException {
        String sql = "SELECT u.* FROM administrador a JOIN usuario u ON u.id = a.id WHERE a.id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Administrador a = new Administrador();
                a.setId(rs.getLong("id"));
                a.setNome(rs.getString("nome"));
                a.setCpf(rs.getString("cpf"));
                a.setTelefone(rs.getString("telefone"));
                a.setEmail(rs.getString("email"));
                a.setTipo(rs.getInt("tipo"));
                return a;
            }
        }
    }

    public List<Especialidade> listarEspecialidades(long idProfissional) throws SQLException {
        String sql = "SELECT e.* FROM especialidade e " +
                     "JOIN profissional_especialidade pe ON pe.id_especialidade = e.id_especialidade " +
                     "WHERE pe.id_profissional = ? ORDER BY e.nome";
        List<Especialidade> lista = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idProfissional);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Especialidade e = new Especialidade();
                    e.setIdEspecialidade(rs.getLong("id_especialidade"));
                    e.setNome(rs.getString("nome"));
                    e.setDescricao(rs.getString("descricao"));
                    lista.add(e);
                }
            }
        }
        return lista;
    }

    private Profissional montarProfissional(ResultSet rs) throws SQLException {
        Profissional p = new Profissional();
        p.setId(rs.getLong("id"));
        p.setIdProfissional(rs.getLong("id"));
        p.setNome(rs.getString("nome"));
        p.setCpf(rs.getString("cpf"));
        p.setTelefone(rs.getString("telefone"));
        p.setEmail(rs.getString("email"));
        p.setPercentualComissao(rs.getDouble("percentual_comissao"));
        p.setStatusProfissional(rs.getString("status_profissional"));
        p.setAtivo(rs.getBoolean("ativo"));
        p.setTipo(rs.getInt("tipo"));
        return p;
    }
}
