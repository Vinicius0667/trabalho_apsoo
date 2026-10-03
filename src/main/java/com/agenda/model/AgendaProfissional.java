package com.agenda.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class AgendaProfissional {
    private long idAgenda;
    private LocalDate data;
    private LocalTime horarioInicio;
    private LocalTime horarioFim;
    private String status;

    public AgendaProfissional() {}

    public long getIdAgenda() {
        return idAgenda;
    }

    public void setIdAgenda(long idAgenda) {
        this.idAgenda = idAgenda;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
