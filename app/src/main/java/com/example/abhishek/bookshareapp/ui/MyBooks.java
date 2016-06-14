package com.example.abhishek.bookshareapp.ui;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.abhishek.bookshareapp.api.models.UserInfo;
import com.example.abhishek.bookshareapp.ui.adapter.LocalBooksAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyBooks extends AppCompatActivity {

    List<Book> booksList;
    LocalBooksAdapter adapter;
    LinearLayoutManager nLinearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_books);
        RecyclerView localBooksList = (RecyclerView) findViewById(R.id.localBooksLists);
        nLinearLayoutManager = new LinearLayoutManager(this);
        nLinearLayoutManager.setReverseLayout(true);
        nLinearLayoutManager.setStackFromEnd(true);

        localBooksList.setLayoutManager(nLinearLayoutManager);
        booksList = new ArrayList<>();
        adapter = new LocalBooksAdapter(this, booksList, new LocalBooksAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Book book) {
                Log.i("Item", "onItemClick");
            }
        });

        SharedPreferences preferences = getSharedPreferences("Token", MODE_PRIVATE);
        String id = preferences.getString("id", "");

        localBooksList.setAdapter(adapter);
        getUserBookList(id);

        FloatingActionButton button= (FloatingActionButton) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(MyBooks.this,SearchResultsActivity.class);
                startActivity(i);
                finish();
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

    public void getUserBookList(String id){
        UsersAPI api = NetworkingFactory.getLocalInstance().getUsersAPI();
        Call<UserInfo> call = api.getUserDetails(id);
        call.enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                if(response.body()!=null) {
                    Log.d("UserProfile Response:", response.toString());

                    List<Book> booksTempInfoList = response.body().getUserBookList();
                    booksList.clear();
                    booksList.addAll(booksTempInfoList);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                Log.d("BookDetails fail", t.toString());
            }
        });
    }
}
