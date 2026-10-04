package com.agenda.model;

public class ItemAgendamento {
    private long id;
    private int duracaoRealizada;
    private float valorCobrado;

    private Servico servico;

    public ItemAgendamento() {}

    public ItemAgendamento(Servico servico) {
        this.servico = servico;
        this.duracaoRealizada = servico.getDuracaoMinutos();
        this.valorCobrado = (float) servico.getValorPadrao();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getDuracaoRealizada() {
        return duracaoRealizada;
    }

    public void setDuracaoRealizada(int duracaoRealizada) {
        this.duracaoRealizada = duracaoRealizada;
    }

    public float getValorCobrado() {
        return valorCobrado;
    }

    public void setValorCobrado(float valorCobrado) {
        this.valorCobrado = valorCobrado;
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
    }
}
