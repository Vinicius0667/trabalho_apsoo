package com.agenda.model;

import java.util.ArrayList;
import java.util.List;

public class Profissional extends Usuario {
    private long idProfissional;
    private double percentualComissao;
    private String statusProfissional;
    private boolean ativo;

    private List<Especialidade> especialidades = new ArrayList<>();
    private List<AgendaProfissional> agendas = new ArrayList<>();
    private List<Agendamento> agendamentos = new ArrayList<>();

    public Profissional() {
        super();
    }

    public long getIdProfissional() {
        return idProfissional;
    }

    public void setIdProfissional(long idProfissional) {
        this.idProfissional = idProfissional;
    }

    public double getPercentualComissao() {
        return percentualComissao;
    }

    public void setPercentualComissao(double percentualComissao) {
        this.percentualComissao = percentualComissao;
    }

    public String getStatusProfissional() {
        return statusProfissional;
    }

    public void setStatusProfissional(String statusProfissional) {
        this.statusProfissional = statusProfissional;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public List<Especialidade> getEspecialidades() {
        return especialidades;
    }

    public void setEspecialidades(List<Especialidade> especialidades) {
        this.especialidades = especialidades;
    }

    public List<AgendaProfissional> getAgendas() {
        return agendas;
    }

    public void setAgendas(List<AgendaProfissional> agendas) {
        this.agendas = agendas;
    }

    public List<Agendamento> getAgendamentos() {
        return agendamentos;
    }

    public void setAgendamentos(List<Agendamento> agendamentos) {
        this.agendamentos = agendamentos;
    }
}
