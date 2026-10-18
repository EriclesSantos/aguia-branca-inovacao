package com.nordhub.aguiachallenge.models;

public class User {

    private String uid;
    private String nome;
    private String email;
    private String tipo;

    public User() {
    }

    public User(String uid, String nome, String email, String tipo) {
        this.uid = uid;
        this.nome = nome;
        this.email = email;
        this.tipo = tipo;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}