package com.nordhub.aguiachallenge.models;

public class Strategy {

    private String titulo;
    private String descricao;

    public Strategy() {
    }

    public Strategy(String titulo, String descricao) {
        this.titulo = titulo;
        this.descricao = descricao;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }
}