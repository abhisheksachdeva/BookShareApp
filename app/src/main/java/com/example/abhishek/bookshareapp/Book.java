package com.example.abhishek.bookshareapp;

/**
 * Created by abhishek on 30/1/16.
 */

public class Book {
    private int ISBN;
    private String title;

    public int getISBN(){
        return ISBN;
    }

    public void setISBN(int ISBN){
        this.ISBN=ISBN;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title=title;
    }
}
