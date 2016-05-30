package com.example.abhishek.bookshareapp.ui;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.abhishek.bookshareapp.R;
import com.example.abhishek.bookshareapp.api.NetworkingFactory;
import com.example.abhishek.bookshareapp.api.UsersAPI;
import com.example.abhishek.bookshareapp.api.models.LocalBooks.Book;
import com.example.abhishek.bookshareapp.ui.adapter.LocalBooksAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyBooks extends AppCompatActivity {

    List<Book> booksList;
    LocalBooksAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_books);

        RecyclerView localBooksList = (RecyclerView) findViewById(R.id.localBooksLists);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        localBooksList.setLayoutManager(layoutManager);
        booksList = new ArrayList<>();
        adapter = new LocalBooksAdapter(this, booksList, new LocalBooksAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Book book) {
                Log.i("Item", "onItemClick");
            }
        });
        localBooksList.setAdapter(adapter);

        getLocalBooks();


        FloatingActionButton button= (FloatingActionButton) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(MyBooks.this,SearchResultsActivity.class);
                startActivity(i);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i= new Intent(MyBooks.this,MainActivity.class);
        startActivity(i);
        finish();

    }

    public void getLocalBooks() {

        UsersAPI api = NetworkingFactory.getLocalInstance().getUsersAPI();
        Call<List<Book>> call = api.getBooksList();
        call.enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                if(response.body()!=null) {
                    Log.d("Search Response:", response.toString());
                    List<com.example.abhishek.bookshareapp.api.models.LocalBooks.Book> localBooksList = response.body();
                    booksList.clear();
                    booksList.addAll(localBooksList);

                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onFailure(Call<List<Book>> call, Throwable t) {
                Log.d("searchresp","searchOnFail "+ t.toString());
            }
        });

    }
}
