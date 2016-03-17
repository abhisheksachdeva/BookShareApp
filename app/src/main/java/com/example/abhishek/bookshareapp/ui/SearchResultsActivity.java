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
import com.example.abhishek.bookshareapp.api.models.GoodreadsResponse;
import com.example.abhishek.bookshareapp.api.models.Search;
import com.example.abhishek.bookshareapp.ui.adapter.BooksAdapter;
import com.example.abhishek.bookshareapp.utils.CommonUtilities;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by abhishek on 13/2/16.
 */
public class SearchResultsActivity extends AppCompatActivity {
    String id;
    String query;
    List<Book> bookList;
    BooksAdapter adapter;
    ListView resultsList;
    String API_KEY= CommonUtilities.API_KEY;
    Search sr;
    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_results);

        resultsList= (ListView)findViewById(R.id.results_list);
        Intent i= getIntent();
        query=i.getStringExtra("query");
        Log.d("querysearch", query);
        getBooks(query,API_KEY);

    }

    public void getBooks(String query,String key) {

        Toast.makeText(SearchResultsActivity.this,"getbooks",Toast.LENGTH_SHORT).show();

        BooksAPI api = NetworkingFactory.getInstance().getBooksApi();
        Call<GoodreadsResponse> call = api.getBooks(query, key);
        call.enqueue(new Callback<GoodreadsResponse>() {
            @Override
            public void onResponse(Call<GoodreadsResponse> call, Response<GoodreadsResponse> response) {
                Log.d("searchresp",response.toString());
                sr= response.body().getSearch();
                bookList= sr.getBooks();
                Log.d("searchresp",bookList.toString());
                adapter= new BooksAdapter(SearchResultsActivity.this,bookList);
                resultsList.setAdapter(adapter);

                Toast.makeText(SearchResultsActivity.this,sr.getBooks().get(0).getBookDetails().getAuthor().getAuthor_name(),Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<GoodreadsResponse> call, Throwable t) {
                Log.d("searchresp","searchOnFail "+ t.toString());

            }
        });

    }

}

