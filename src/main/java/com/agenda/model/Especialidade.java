package com.agenda.model;

public class Especialidade {
    private long idEspecialidade;
    private String descricao;
    private String nome;

    public Especialidade() {}

    public long getIdEspecialidade() {
        return idEspecialidade;
    }

    public void setIdEspecialidade(long idEspecialidade) {
        this.idEspecialidade = idEspecialidade;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return nome;
    }
}
