package com.example.gereciamentoeventos;

public class Local {
    private int id;
    private String nome_local;
    private String endereco;
    private int capacidade;

    public Local() {
    }

    public Local(String nome_local, String endereco, int capacidade) {
        this.nome_local = nome_local;
        this.endereco = endereco;
        this.capacidade = capacidade;
    }

    public Local(int id, String nome_local, String endereco, int capacidade) {
        this.id = id;
        this.nome_local = nome_local;
        this.endereco = endereco;
        this.capacidade = capacidade;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome_local() {
        return nome_local;
    }

    public void setNome_local(String nome_local) {
        this.nome_local = nome_local;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(int capacidade) {
        this.capacidade = capacidade;
    }
}


