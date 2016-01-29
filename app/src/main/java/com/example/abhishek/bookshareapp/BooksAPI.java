package com.example.abhishek.bookshareapp;

/**
 * Created by abhishek on 30/1/16.
 */

import java.util.List;

import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Path;


public interface BooksAPI {

    @GET("/books/v1/volumes?q=isbn:{isbn}")
    public void getBooks(Callback<List<Book>> response);

}
