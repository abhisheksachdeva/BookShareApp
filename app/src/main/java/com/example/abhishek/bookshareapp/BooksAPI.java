package com.example.abhishek.bookshareapp;

/**
 * Created by abhishek on 30/1/16.
 */

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface BooksAPI {

    @GET("books/v1/volumes")
    Call<BookResponse> getBooks(
            @Query("q") String isbn
    );

}
