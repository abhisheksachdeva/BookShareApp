package com.example.abhishek.bookshareapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import static com.example.abhishek.bookshareapp.CommonUtilities.google_api_url;

/**
 * Created by abhishek on 13/2/16.
 */
public class SearchResults extends AppCompatActivity {
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

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(google_api_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BooksAPI api = retrofit.create(BooksAPI.class);

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
