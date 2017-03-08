package com.sdsmdg.bookshareapp.BSA.api.models.LocalBooks;

import java.util.List;


public class BookList {

    List<Book> results;
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