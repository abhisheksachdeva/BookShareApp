package com.example.abhishek.bookshareapp.api;

import com.example.abhishek.bookshareapp.utils.CommonUtilities;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class NetworkingFactory {

    BooksAPI booksAPI;
    UsersAPI usersAPI;
    Retrofit retrofit;

    private static NetworkingFactory grInstance = new NetworkingFactory(CommonUtilities.goodreads_api_url, false);
    private static NetworkingFactory localInstance = new NetworkingFactory(CommonUtilities.local_books_api_url, true);

    public static NetworkingFactory getGRInstance() {
        return grInstance;
    }

    public static NetworkingFactory getLocalInstance() {
        return localInstance;
    }

    private NetworkingFactory(String url, boolean json) {
        OkHttpClient.Builder httpclient = new OkHttpClient.Builder();

        if (json) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpclient.build())
                    .build();
        } else {
            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(SimpleXmlConverterFactory.create())
                    .client(httpclient.build())
                    .build();
        }
    }

    public BooksAPI getBooksApi() {
        booksAPI = retrofit.create(BooksAPI.class);
        return booksAPI;
    }

    public UsersAPI getUsersAPI() {
        usersAPI = retrofit.create(UsersAPI.class);
        return usersAPI;
    }
}
