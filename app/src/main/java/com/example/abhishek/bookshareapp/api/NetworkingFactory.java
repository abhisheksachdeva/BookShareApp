package com.example.abhishek.bookshareapp.api;

import com.example.abhishek.bookshareapp.api.models.BookDetails;
import com.example.abhishek.bookshareapp.utils.CommonUtilities;

import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

import static com.example.abhishek.bookshareapp.utils.CommonUtilities.goodreads_api_url;

/**
 * Created by manchanda on 1/3/16.
 */
public class NetworkingFactory {

    BooksAPI api;
    BookDetailsAPI api2;

    private static NetworkingFactory ourInstance = new NetworkingFactory(CommonUtilities.goodreads_api_url);

    public static NetworkingFactory getInstance() {
        return ourInstance;
    }

    private NetworkingFactory(String googleApiUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(googleApiUrl)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        api = retrofit.create(BooksAPI.class);
        api2 = retrofit.create(BookDetailsAPI.class);
    }

    public BooksAPI getBooksApi(){
        return api;
    }
    public BookDetailsAPI getBookDetailsApi(){
        return api2;
    }

}
