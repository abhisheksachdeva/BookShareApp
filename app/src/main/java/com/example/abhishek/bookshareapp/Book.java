package com.example.abhishek.bookshareapp;

import java.util.List;

/**
 * Created by abhishek on 30/1/16.
 */

public class Book {
    private int ISBN;
    private String title;
    private String id;
    VolumeInfo volumeInfo;
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

    public void setID(String id){
        this.id=id;
    }

    public String getId() {
        return id;
    }

    public VolumeInfo getInfo(){
        return volumeInfo;
    }
}


