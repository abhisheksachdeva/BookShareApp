package com.example.abhishek.bookshareapp.api.models;

import java.util.List;

/**
 * Created by abhishek on 10/2/16.
 */
public class VolumeInfo {
    private String title;
    private List<String > authors;

    public String getTitle(){
        return title;
    }
    public String getAllAuthors(){
        String allAuthors="";
        for(int i=0;i<authors.size();i++) {
            allAuthors+=authors.get(i);
            if(i<authors.size()-1){
                allAuthors+=" & ";
            }
        }
        return allAuthors;

    }
}

