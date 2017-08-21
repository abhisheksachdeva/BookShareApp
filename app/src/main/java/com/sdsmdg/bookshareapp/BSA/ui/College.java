package com.sdsmdg.bookshareapp.BSA.ui;

/**
 * Created by Harshit Bansal on 5/16/2017.
 */

public class College {

    private String collegeName;
    private String collegeDomain;

    public College(){};

    public College(String collegeName, String collegeDomain) {
        this.collegeName = collegeName;
        this.collegeDomain = collegeDomain;
    }

    public String getCollegeDomain() {
        return collegeDomain;
    }

    public void setCollegeDomain(String collegeDomain) {
        this.collegeDomain = collegeDomain;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }
}