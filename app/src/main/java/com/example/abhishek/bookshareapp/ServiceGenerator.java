package com.example.abhishek.bookshareapp;

import okhttp3.OkHttpClient;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

import static com.example.abhishek.bookshareapp.utils.CommonUtilities.google_api_url;
/**
 * Created by abhishek on 23/1/16.
 */
public class ServiceGenerator {

    static String API_BASE_URL = google_api_url+"books/v1/volumes?q=isbn:9780553819229";

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
