package com.sdsmdg.bookshareapp.BSA.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;

import com.sdsmdg.bookshareapp.BSA.Listeners.EndlessScrollListener;
import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.api.NetworkingFactory;
import com.sdsmdg.bookshareapp.BSA.api.UsersAPI;
import com.sdsmdg.bookshareapp.BSA.api.models.LocalBooks.Book;
import com.sdsmdg.bookshareapp.BSA.api.models.LocalBooks.BookList;
import com.sdsmdg.bookshareapp.BSA.ui.adapter.Local.BooksAdapterSimple;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GuestActivity extends AppCompatActivity {

    final String TAG = GuestActivity.class.getSimpleName();

    FrameLayout rootView;
    RecyclerView localBookList;
    List<Book> booksList = new ArrayList<>();
    BooksAdapterSimple adapter;
    SwipeRefreshLayout refreshLayout;
    LinearLayoutManager layoutManager;
    View.OnClickListener signupclicklistener;
    CustomProgressDialog customProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest);
        customProgressDialog = new CustomProgressDialog(GuestActivity.this);
        customProgressDialog.setCancelable(false);
        customProgressDialog.show();

        rootView = (FrameLayout) findViewById(R.id.root_view);

        adapter = new BooksAdapterSimple(this, booksList, new BooksAdapterSimple.OnItemClickListener() {
            @Override
            public void onItemClick(Book book) {
                Snackbar snbar = Snackbar.make(findViewById(R.id.frameLayout), "Create a new account ! ", Snackbar.LENGTH_SHORT)
                        .setAction("Sign Up", signupclicklistener);
                snbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
                snbar.show();
                snbar.show();

            }
        });

        signupclicklistener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(GuestActivity.this, SignupActivity.class);
                startActivity(i);
            }
        };

        layoutManager = new LinearLayoutManager(this);
        localBookList = (RecyclerView) findViewById(R.id.local_books_list);
        localBookList.setLayoutManager(layoutManager);
        localBookList.setAdapter(adapter);

        final EndlessScrollListener endlessScrollListener = new EndlessScrollListener((LinearLayoutManager) layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                getLocalBooks(String.valueOf(page + 1));
            }
        };

        localBookList.addOnScrollListener(endlessScrollListener);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_to_refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                endlessScrollListener.reset();
                getLocalBooks("1");
            }

        });

        getLocalBooks("1");
    }

    public void getLocalBooks(final String page) {
        UsersAPI api = NetworkingFactory.getLocalInstance().getUsersAPI();
        Call<BookList> call = api.getGuestBList(page);
        call.enqueue(new Callback<BookList>() {
            @Override
            public void onResponse(Call<BookList> call, Response<BookList> response) {
                if (response.body() != null) {
                    List<Book> localBooksList = response.body().getResults();
                    if (page.equals("1")) {
                        booksList.clear();
                        adapter.notifyDataSetChanged();
                    }
                    booksList.addAll(localBooksList);
                    adapter.notifyDataSetChanged();
                    refreshLayout.setRefreshing(false);
                }

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        customProgressDialog.dismiss();
                    }
                }, 1000);

            }

            @Override
            public void onFailure(Call<BookList> call, Throwable t) {
                refreshLayout.setRefreshing(false);
                customProgressDialog.dismiss();

            }
        });

    }
}
