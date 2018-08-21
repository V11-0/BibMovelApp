package com.bibmovel.client.object;

import java.util.Date;

public class Book {
    String nome;
    String autor;
    String Descrição;
    Date lançamento;

    public Book(String nome, String autor, String descrição, Date lançamento) {
        this.nome = nome;
        this.autor = autor;
        Descrição = descrição;
        this.lançamento = lançamento;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getDescrição() {
        return Descrição;
    }

    public void setDescrição(String descrição) {
        Descrição = descrição;
    }

    public Date getLançamento() {
        return lançamento;
    }

    public void setLançamento(Date lançamento) {
        this.lançamento = lançamento;
    }
}
