package com.nordhub.aguiachallenge.models;

public class Project {

    private String nome;
    private String status;
    private String investimento;
    private String retorno;
    private String prazo;

    public Project() {
    }

    public Project(String nome,
                   String status,
                   String investimento,
                   String retorno,
                   String prazo) {

        this.nome = nome;
        this.status = status;
        this.investimento = investimento;
        this.retorno = retorno;
        this.prazo = prazo;
    }

    public String getNome() {
        return nome;
    }

    public String getStatus() {
        return status;
    }

    public String getInvestimento() {
        return investimento;
    }

    public String getRetorno() {
        return retorno;
    }

    public String getPrazo() {
        return prazo;
    }
}