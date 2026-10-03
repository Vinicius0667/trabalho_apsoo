package com.agenda.dao;

import com.agenda.model.RepasseComissao;
import com.agenda.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RepasseComissaoDAO {

    public void inserir(long idAgendamento, RepasseComissao repasse) throws SQLException {
        String sql = "INSERT INTO repasse_comissao " +
                     "(id_agendamento, valor_pago_cliente, percentual_aplicado, valor_comissao, data_calculo, status_pagamento) " +
                     "VALUES (?, ?, ?, ?, ?, ?) RETURNING id_repasse";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idAgendamento);
            stmt.setFloat(2, repasse.getValorPagoCliente());
            stmt.setFloat(3, repasse.getPercentualAplicado());
            stmt.setDouble(4, repasse.getValorComissao());
            stmt.setObject(5, repasse.getDataCalculo());
            stmt.setString(6, repasse.getStatusPagamento());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    repasse.setIdRepasse(rs.getLong("id_repasse"));
                }
            }
        }
    }
}
