package com.sdsmdg.bookshareapp.BSA.api;

import com.sdsmdg.bookshareapp.BSA.api.models.GoodreadsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BooksAPI {

    @GET("search.xml")
    Call<GoodreadsResponse> getBooks(
        @Query("q") String keyword,
        @Query("search[field]") String field,
        @Query("key") String key
    );

}


