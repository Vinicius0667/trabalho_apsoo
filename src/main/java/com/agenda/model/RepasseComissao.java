package com.agenda.model;

import java.time.LocalDateTime;

public class RepasseComissao {
    private long idRepasse;
    private float valorPagoCliente;
    private float percentualAplicado;
    private double valorComissao;
    private LocalDateTime dataCalculo;
    private LocalDateTime dataRepasse;
    private String statusPagamento;

    public RepasseComissao() {}

    public long getIdRepasse() {
        return idRepasse;
    }

    public void setIdRepasse(long idRepasse) {
        this.idRepasse = idRepasse;
    }

    public float getValorPagoCliente() {
        return valorPagoCliente;
    }

    public void setValorPagoCliente(float valorPagoCliente) {
        this.valorPagoCliente = valorPagoCliente;
    }

    public float getPercentualAplicado() {
        return percentualAplicado;
    }

    public void setPercentualAplicado(float percentualAplicado) {
        this.percentualAplicado = percentualAplicado;
    }

    public double getValorComissao() {
        return valorComissao;
    }

    public void setValorComissao(double valorComissao) {
        this.valorComissao = valorComissao;
    }

    public LocalDateTime getDataCalculo() {
        return dataCalculo;
    }

    public void setDataCalculo(LocalDateTime dataCalculo) {
        this.dataCalculo = dataCalculo;
    }

    public LocalDateTime getDataRepasse() {
        return dataRepasse;
    }

    public void setDataRepasse(LocalDateTime dataRepasse) {
        this.dataRepasse = dataRepasse;
    }

    public String getStatusPagamento() {
        return statusPagamento;
    }

    public void setStatusPagamento(String statusPagamento) {
        this.statusPagamento = statusPagamento;
    }
}
