package com.example.abhishek.bookshareapp;

import java.util.List;

/**
 * Created by abhishek on 10/2/16.
 */
public class BookResponse {

    private String kind;

    private int totalItems;

    private List<Book> items;

    public String getKind() {
        return kind;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public List<Book> getItems() {
        return items;
    }
}
