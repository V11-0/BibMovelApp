package com.bibmovel.client.model.vo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Livro {

    @SerializedName("titulo") private String titulo;
    @SerializedName("isbn") private String isbn;
    @SerializedName("nomeArquivo") private String nomeArquivo;
	@SerializedName("genero") private String genero;
	@SerializedName("anoPublicacao") private short anoPublicacao;
    private Editora editora;
    @SerializedName("classificacaoMedia") private float classificacaoMedia;
    private List<Autor> autores;

    public Livro() {}

    public Livro(String titulo, String isbn, String nomeArquivo, String genero, short anoPublicacao) {
        this.titulo = titulo;
        this.isbn = isbn;
        this.nomeArquivo = nomeArquivo;
        this.genero = genero;
        this.anoPublicacao = anoPublicacao;
    }

    public Livro(String titulo, String nomeArquivo, float classificacaoMedia) {
        this.titulo = titulo;
        this.nomeArquivo = nomeArquivo;
        this.classificacaoMedia = classificacaoMedia;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public short getAnoPublicacao() {
        return anoPublicacao;
    }

    public void setAnoPublicacao(short anoPublicacao) {
        this.anoPublicacao = anoPublicacao;
    }

    public Editora getEditora() {
        return editora;
    }

    public void setEditora(Editora editora) {
        this.editora = editora;
    }

    public float getClassificacaoMedia() {
        return classificacaoMedia;
    }

    public void setClassificacaoMedia(float classificacaoMedia) {
        this.classificacaoMedia = classificacaoMedia;
    }

    public List<Autor> getAutores() {
        return autores;
    }

    public void setAutores(List<Autor> autores) {
        this.autores = autores;
    }
}
