package com.agenda.model;

public enum StatusAgendamento {
    AGENDADO(1),
    BLOQUEADO(2),
    CANCELADO(3),
    PENDENTE(4),
    CONCLUIDO(5);

    private final int codigo;

    StatusAgendamento(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }

    public static StatusAgendamento fromCodigo(int codigo) {
        for (StatusAgendamento status : values()) {
            if (status.codigo == codigo) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status de agendamento inválido: " + codigo);
    }
}
