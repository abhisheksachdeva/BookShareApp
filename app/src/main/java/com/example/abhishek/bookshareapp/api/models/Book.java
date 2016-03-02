package com.example.abhishek.bookshareapp.api.models;

/**
 * Created by abhishek on 30/1/16.
 */

public class Book {
    private int ISBN;
    private String title;
    private String author;
    private String id;
    VolumeInfo volumeInfo;
    public int getISBN(){
        return ISBN;
    }

    public void setISBN(int ISBN){
        this.ISBN=ISBN;
    }

    public String getAuthor(){
        return author;
    }

    public void setAuthor(String author){
        this.author=author;
    }

    public void setID(String id){
        this.id=id;
    }

    public String getId() {
        return id;
    }

    public void setVolumeInfo(VolumeInfo volumeInfo){
        this.volumeInfo=volumeInfo;
    }

    public VolumeInfo getVolumeInfo(){
        return volumeInfo;
    }
}


