package com.example.abhishek.bookshareapp.api;

/**
 * Created by ajayrahul on 14/5/16.
 */
import com.example.abhishek.bookshareapp.api.models.GoodreadsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BookDetailsAPI {

    @GET("show.xml")
    Call<GoodreadsResponse> getBooksDetails(
            @Query("id") Integer search_id,
            @Query("key") String key
    );

}

