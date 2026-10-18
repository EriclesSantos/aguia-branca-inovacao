package com.nordhub.aguiachallenge.models;

public class Idea {

    private String titulo;
    private String descricao;
    private String status;
    private String usuarioId;

    public Idea() {
    }

    public Idea(String titulo,
                String descricao,
                String status,
                String usuarioId) {

        this.titulo = titulo;
        this.descricao = descricao;
        this.status = status;
        this.usuarioId = usuarioId;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getStatus() {
        return status;
    }

    public String getUsuarioId() {
        return usuarioId;
    }
}