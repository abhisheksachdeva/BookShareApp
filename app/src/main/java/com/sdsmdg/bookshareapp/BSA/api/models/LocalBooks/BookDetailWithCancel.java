package com.sdsmdg.bookshareapp.BSA.api.models.LocalBooks;

import java.util.List;

public class BookDetailWithCancel {


    Book book;
    List<Boolean> cancels;

    public List<Boolean> getCancels() {
        return cancels;
    }

    int count;

    public int getCount() {
        return count;
    }

    public Book getBook() {
        return book;
    }

}
