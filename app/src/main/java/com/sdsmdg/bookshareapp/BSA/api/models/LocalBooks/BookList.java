package com.sdsmdg.bookshareapp.BSA.api.models.LocalBooks;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class BookList {

    @SerializedName("results")
    List<Book> results;
    @SerializedName("cancels")
    List<Boolean> cancels;

    public List<Boolean> getCancels() {
        return cancels;
    }

    int count;

    public int getCount() {
        return count;
    }

    public List<Book> getResults() {
        return results;
    }
}