package com.bibmovel.client.model.vo;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class Autor {

    @SerializedName("nome") private String nome;
    @SerializedName("dataNascimento") private Date dataNascimento;
    @SerializedName("nacionalidade") private String nacionalidade;
    @SerializedName("id") private Integer id;
	private List<Livro> livros;

	public Autor() {}

    public Autor(String nome, Date dataNascimento, String nacionalidade, Integer id) {
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.nacionalidade = nacionalidade;
        this.id = id;
    }

    public Autor(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getNacionalidade() {
        return nacionalidade;
    }

    public void setNacionalidade(String nacionalidade) {
        this.nacionalidade = nacionalidade;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Livro> getLivros() {
        return livros;
    }

    public void setLivros(List<Livro> livros) {
        this.livros = livros;
    }
}
