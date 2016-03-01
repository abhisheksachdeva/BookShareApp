package com.example.abhishek.bookshareapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhishek.bookshareapp.R;
import com.example.abhishek.bookshareapp.api.NetworkingFactory;
import com.example.abhishek.bookshareapp.api.models.VolumeInfo;
import com.example.abhishek.bookshareapp.api.BooksAPI;
import com.example.abhishek.bookshareapp.api.models.Book;
import com.example.abhishek.bookshareapp.api.models.BookResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by abhishek on 13/2/16.
 */
public class SearchResultsActivity extends AppCompatActivity {
    String id;
    String query;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_results);
        ListView results_list= (ListView)findViewById(R.id.results_list);
        TextView test =(TextView)findViewById(R.id.testing);
        test.setText("Check");
        Intent i= getIntent();
        query=i.getStringExtra("query");
        Log.d("querysearch", query);
        getBooks(query);

    }

    public void getBooks(String query){

//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(google_api_url)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();

//        BooksAPI api = retrofit.create(BooksAPI.class);

        BooksAPI api = NetworkingFactory.getInstance().getBooksApi();
        Call<BookResponse> call = api.getBooks(query);
        call.enqueue(new Callback<BookResponse>() {
            @Override
            public void onResponse(retrofit2.Response<BookResponse> response) {
                List<Book> list = response.body().getItems();

                Log.i("searchlist", String.valueOf(list.size()));
                Book bk = list.get(0);
                 id = bk.getId();
                VolumeInfo vinfo1 = bk.getInfo();
                try {
                    Log.d("searchvinfo", vinfo1.getTitle());
                } catch (Exception e) {
                    Log.d("searchabcd", e.toString());
                }
                Log.i("searchresp", id);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i("searchfail", "no resp");
            }
        });
        Toast.makeText(this ,id,Toast.LENGTH_SHORT).show();

    }

}
