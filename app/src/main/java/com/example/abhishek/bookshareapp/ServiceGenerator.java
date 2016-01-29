package com.example.abhishek.bookshareapp;

import okhttp3.OkHttpClient;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.CallAdapter;

/**
 * Created by abhishek on 23/1/16.
 */
public class ServiceGenerator {

    public static final String API_BASE_URL = "https://www.googleapis.com/books/v1/volumes?q=isbn:9780553819229";

    private static OkHttpClient httpClient = new OkHttpClient();
    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());


    public static <S> S createService(Class<S> serviceClass) {
        Retrofit retrofit = builder.client(httpClient).build();
        return retrofit.create(serviceClass);
    }
}
