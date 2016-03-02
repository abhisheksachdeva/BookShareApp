package com.example.abhishek.bookshareapp.api.models;

import java.util.List;

/**
 * Created by abhishek on 30/1/16.
 */

public class Book {
    private int ISBN;

    private String id;
    VolumeInfo volumeInfo;
    List<String> authors;
    public int getISBN(){
        return ISBN;
    }

    public void setISBN(int ISBN){
        this.ISBN=ISBN;
    }

    public List<String> getAuthors(){
        return authors;
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


