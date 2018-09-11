package com.bibmovel.client.objects;

import android.graphics.Color;

import java.util.Date;

public class Book {

    private String isbn;
    private String name;
    private String author;
    private String description;
    private Date launch;
    private float rating;
    private String coverColor;

    public Book(String isbn, String name, String author, String description, Date launch, float rating, String coverColor) {
        this.isbn = isbn;
        this.name = name;
        this.author = author;
        this.description = description;
        this.launch = launch;
        this.rating = rating;
        this.coverColor = coverColor;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getLaunch() {
        return launch;
    }

    public void setLaunch(Date launch) {
        this.launch = launch;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getCoverColor() {
        return coverColor;
    }

    public void setCoverColor(String coverColor) {
        this.coverColor = coverColor;
    }
}
