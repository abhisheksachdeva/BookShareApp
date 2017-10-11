package com.sdsmdg.bookshareapp.BSA.api;

import android.content.Context;

import com.readystatesoftware.chuck.ChuckInterceptor;
import com.sdsmdg.bookshareapp.BSA.utils.CommonUtilities;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class NetworkingFactory {

    private BooksAPI booksAPI;
    private UsersAPI usersAPI;
    private Retrofit retrofit;

    private static NetworkingFactory grInstance;
    private static NetworkingFactory localInstance;

    public static void init(Context context) {
        grInstance = new NetworkingFactory(context, CommonUtilities.goodreads_api_url, false);
        localInstance = new NetworkingFactory(context, CommonUtilities.local_books_api_url, true);
    }

    public static NetworkingFactory getGRInstance() {
        return grInstance;
    }

    public static NetworkingFactory getLocalInstance() {
        return localInstance;
    }

    private NetworkingFactory(Context context, String url, boolean json) {
        OkHttpClient.Builder httpclient = new OkHttpClient.Builder().addInterceptor(new ChuckInterceptor(context));
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
