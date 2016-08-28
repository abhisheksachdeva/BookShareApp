package com.sdsmdg.bookshareapp.BSA.api;

import com.sdsmdg.bookshareapp.BSA.api.models.GoodreadsResponse;
import com.sdsmdg.bookshareapp.BSA.api.models.GoodreadsResponse2;
import com.sdsmdg.bookshareapp.BSA.api.models.GoodreadsResponse3;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BooksAPI {

    @GET("search.xml")
    Call<GoodreadsResponse> getBooks(
        @Query("q") String keyword,
        @Query("search[field]") String field,
        @Query("key") String key
    );


    @GET("book/show.xml")
    Call<GoodreadsResponse2> getBookDescription(
            @Query("id") Integer id,
            @Query("key") String key
    );

    @GET("review/list.xml")
    Call<GoodreadsResponse3> getToRead(
            @Path("id") String id,
            @Query("shelf") String shelf,
            @Query("key") String key
    );




}


