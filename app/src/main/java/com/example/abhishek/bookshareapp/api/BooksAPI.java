package com.example.abhishek.bookshareapp.api;

/**
 * Created by abhishek on 30/1/16.
 */

import com.example.abhishek.bookshareapp.api.models.GoodreadsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface BooksAPI {

    @GET("search.xml")
    Call<GoodreadsResponse> getBooks(
        @Query("q") String keyword,
        @Query("key") String key
    );

}
