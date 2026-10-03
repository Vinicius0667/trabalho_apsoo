package com.agenda.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Agendamento {
    private long id;
    private LocalDate dataAgendada;
    private LocalTime horarioInicio;
    private LocalTime horarioFim;
    private StatusAgendamento status;
    private String motivoCancelamento;
    private String observacoes;
    private LocalDate dataRealizacao;
    private LocalTime horarioInicioRealizacao;
    private LocalTime horarioFimRealizacao;

    // Cliente 1 solicita 0..* Agendamento
    private Cliente cliente;
    // Profissional que vai atender o agendamento
    private Profissional profissional;
    // Agendamento 1 <>-- 1..* ItemAgendamento (composicao)
    private List<ItemAgendamento> itens = new ArrayList<>();
    // Agendamento 1 gera 0..1 RepasseComissao
    private RepasseComissao repasseComissao;

    public Agendamento() {
        this.status = StatusAgendamento.AGENDADO;
    }

    public void adicionarItem(ItemAgendamento item) {
        itens.add(item);
    }

    public int getDuracaoTotal() {
        int total = 0;
        for (ItemAgendamento item : itens) {
            total += item.getDuracaoRealizada();
        }
        return total;
    }

    public float getValorTotal() {
        float total = 0;
        for (ItemAgendamento item : itens) {
            total += item.getValorCobrado();
        }
        return total;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDate getDataAgendada() {
        return dataAgendada;
    }

    public void setDataAgendada(LocalDate dataAgendada) {
        this.dataAgendada = dataAgendada;
    }

    public LocalTime getHorarioInicio() {
        return horarioInicio;
    }

    public void setHorarioInicio(LocalTime horarioInicio) {
        this.horarioInicio = horarioInicio;
    }

    public LocalTime getHorarioFim() {
        return horarioFim;
    }

    public void setHorarioFim(LocalTime horarioFim) {
        this.horarioFim = horarioFim;
    }

    public StatusAgendamento getStatus() {
        return status;
    }

    public void setStatus(StatusAgendamento status) {
        this.status = status;
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setMotivoCancelamento(String motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public LocalDate getDataRealizacao() {
        return dataRealizacao;
    }

    public void setDataRealizacao(LocalDate dataRealizacao) {
        this.dataRealizacao = dataRealizacao;
    }

    public LocalTime getHorarioInicioRealizacao() {
        return horarioInicioRealizacao;
    }

    public void setHorarioInicioRealizacao(LocalTime horarioInicioRealizacao) {
        this.horarioInicioRealizacao = horarioInicioRealizacao;
    }

    public LocalTime getHorarioFimRealizacao() {
        return horarioFimRealizacao;
    }

    public void setHorarioFimRealizacao(LocalTime horarioFimRealizacao) {
        this.horarioFimRealizacao = horarioFimRealizacao;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Profissional getProfissional() {
        return profissional;
    }

    public void setProfissional(Profissional profissional) {
        this.profissional = profissional;
    }

    public List<ItemAgendamento> getItens() {
        return itens;
    }

    public void setItens(List<ItemAgendamento> itens) {
        this.itens = itens;
    }

    public RepasseComissao getRepasseComissao() {
        return repasseComissao;
    }

    public void setRepasseComissao(RepasseComissao repasseComissao) {
        this.repasseComissao = repasseComissao;
    }
}
