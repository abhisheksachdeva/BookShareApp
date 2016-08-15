package com.sdsmdg.bookshareapp.BSA.api.models.LocalBooks;

import java.util.List;


public class BookList {

    List<Book> results;

    int count;

    public int getCount() {
        return count;
    }

    public List<Book> getResults() {
        return results;
    }
}