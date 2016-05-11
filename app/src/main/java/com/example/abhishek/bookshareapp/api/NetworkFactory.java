package com.example.abhishek.bookshareapp.api;

import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

import static com.example.abhishek.bookshareapp.utils.CommonUtilities.goodreads_api_url;

/**
 * Created by manchanda on 1/3/16.
 */
public class NetworkFactory {

    BooksAPI api;
    public BooksAPI getBooksApi(){

        return api;
    }

    public NetworkFactory(String googleApiUrl){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(googleApiUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(BooksAPI.class);

    }
}
