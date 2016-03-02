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
import com.example.abhishek.bookshareapp.api.BooksAPI;
import com.example.abhishek.bookshareapp.api.models.Book;
import com.example.abhishek.bookshareapp.api.models.BookResponse;
import com.example.abhishek.bookshareapp.ui.adapter.BooksAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by abhishek on 13/2/16.
 */
public class SearchResultsActivity extends AppCompatActivity {
    String id;
    String query;
    List<Book> bookList;
    ListView resultsList;
    BooksAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_results);

        resultsList= (ListView)findViewById(R.id.results_list);
        TextView test =(TextView)findViewById(R.id.testing);

        test.setText("Check");

        Intent i= getIntent();
        query=i.getStringExtra("query");
        Log.d("querysearch", query);
        getBooks(query);

    }

    public void getBooks(String query){

        BooksAPI api = NetworkingFactory.getInstance().getBooksApi();
        Call<BookResponse> call = api.getBooks(query);

        call.enqueue(new Callback<BookResponse>() {
            @Override
            public void onResponse(retrofit2.Response<BookResponse> response) {
                if (response.body().getTotalItems() > 0) {

                    bookList = response.body().getItems();

                    adapter=new BooksAdapter(SearchResultsActivity.this,bookList);
                    Log.d("SearchResultsActivity",adapter.getCount()+"");
                    resultsList.setAdapter(adapter);

                    Log.i("searchlist", bookList.size() + "");

                } else {
                    Log.i("SearchResultsActivity", "No book found");
                }

            }

            @Override
            public void onFailure(Throwable t) {
                Log.i("searchfail", "no resp");
            }
        });
        Toast.makeText(this ,id,Toast.LENGTH_SHORT).show();

    }


    public void onItemClick(int mPosition) {
        Book tempValues = bookList.get(mPosition);
        Toast.makeText(SearchResultsActivity.this, tempValues.getVolumeInfo().getTitle()+tempValues.getVolumeInfo().getAllAuthors(), Toast.LENGTH_SHORT).show();
    }
}
