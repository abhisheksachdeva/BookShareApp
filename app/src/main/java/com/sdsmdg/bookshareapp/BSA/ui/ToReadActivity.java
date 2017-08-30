package com.sdsmdg.bookshareapp.BSA.ui;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.sdsmdg.bookshareapp.BSA.Listeners.EndlessScrollListener;
import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.api.BooksAPI;
import com.sdsmdg.bookshareapp.BSA.api.NetworkingFactory;
import com.sdsmdg.bookshareapp.BSA.api.models.BookDetailsToRead;
import com.sdsmdg.bookshareapp.BSA.api.models.GoodreadsResponse3;
import com.sdsmdg.bookshareapp.BSA.ui.adapter.GR.BooksAdapterToRead;
import com.sdsmdg.bookshareapp.BSA.utils.CommonUtilities;
import com.sdsmdg.bookshareapp.BSA.utils.Helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ToReadActivity extends AppCompatActivity {
    FrameLayout rootView;
    RecyclerView localBookList;
    List<BookDetailsToRead> bookDetailsToReads = new ArrayList<>();
    BooksAdapterToRead adapter;
    LinearLayoutManager layoutManager;
    SwipeRefreshLayout refreshLayout;
    SharedPreferences pref;
    String userGrId;
    CustomProgressDialog customProgressDialog;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_to_read, menu);
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toread);
        customProgressDialog = new CustomProgressDialog(ToReadActivity.this);
        customProgressDialog.setCancelable(false);
        customProgressDialog.show();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pref = getSharedPreferences("UserId", MODE_PRIVATE);
        userGrId = pref.getString("userGrId", null);

        adapter = new BooksAdapterToRead(this, bookDetailsToReads, new BooksAdapterToRead.OnItemClickListener() {
            @Override
            public void onItemClick(BookDetailsToRead booktoRead) {
            }
        });

        layoutManager = new LinearLayoutManager(this);

        localBookList = (RecyclerView) findViewById(R.id.local_books_list);
        localBookList.setLayoutManager(layoutManager);
        localBookList.setAdapter(adapter);

        final EndlessScrollListener endlessScrollListener = new EndlessScrollListener((LinearLayoutManager) layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                getToReadBooks();
                Toast.makeText(getApplicationContext(), "Loading Page " + (page + 1), Toast.LENGTH_SHORT).show();
            }
        };

        localBookList.addOnScrollListener(endlessScrollListener);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_to_refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                endlessScrollListener.reset();
                getToReadBooks();
            }

        });

        getToReadBooks();

    }

    public void getToReadBooks() {
        BooksAPI api = NetworkingFactory.getGRInstance().getBooksApi();
        HashMap<String, String> params = new HashMap<>();
        params.put("key", CommonUtilities.API_KEY);
        params.put("id", userGrId);
        params.put("shelf", "to-read");
        Call<GoodreadsResponse3> call = api.getToRead(params);
        call.enqueue(new Callback<GoodreadsResponse3>() {
            @Override
            public void onResponse(Call<GoodreadsResponse3> call, Response<GoodreadsResponse3> response) {
                if (response.body() != null) {
                    bookDetailsToReads.clear();
                    adapter.notifyDataSetChanged();
                    bookDetailsToReads.addAll(response.body().getBookDetailsToReads());
                    adapter.notifyDataSetChanged();
                    refreshLayout.setRefreshing(false);
                } else {
                }

                final android.os.Handler handler = new android.os.Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        customProgressDialog.dismiss();
                    }
                }, 1000);


            }

            @Override
            public void onFailure(Call<GoodreadsResponse3> call, Throwable t) {
                refreshLayout.setRefreshing(false);
                customProgressDialog.dismiss();

            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_settings:
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("userGrId", null);
                editor.apply();
                Helper.setUserGRid(null);


                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }


}
