package com.bibmovel.client.model.vo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Editora {

    @SerializedName("nome") private String nome;
    @SerializedName("cnpj") private String cnpj;
    private List<Livro> livros;

    public Editora() {
    }

    public Editora(String nome, String cnpj) {
        this.nome = nome;
        this.cnpj = cnpj;
    }

    public Editora(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public List<Livro> getLivros() {
        return livros;
    }

    public void setLivros(List<Livro> livros) {
        this.livros = livros;
    }
}
