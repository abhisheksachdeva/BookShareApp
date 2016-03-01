package com.example.abhishek.bookshareapp.api;

import com.example.abhishek.bookshareapp.utils.CommonUtilities;

import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

import static com.example.abhishek.bookshareapp.utils.CommonUtilities.google_api_url;

/**
 * Created by manchanda on 1/3/16.
 */
public class NetworkingFactory {

    BooksAPI api;

    private static NetworkingFactory ourInstance = new NetworkingFactory(CommonUtilities.google_api_url);

    public static NetworkingFactory getInstance() {
        return ourInstance;
    }

    private NetworkingFactory(String googleApiUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(google_api_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(BooksAPI.class);

    }

    public BooksAPI getBooksApi(){
        return api;
    }
}
