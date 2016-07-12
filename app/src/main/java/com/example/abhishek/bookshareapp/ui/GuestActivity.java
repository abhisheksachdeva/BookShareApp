package com.example.abhishek.bookshareapp.ui;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.abhishek.bookshareapp.Listeners.EndlessScrollListener;
import com.example.abhishek.bookshareapp.R;
import com.example.abhishek.bookshareapp.api.NetworkingFactory;
import com.example.abhishek.bookshareapp.api.UsersAPI;
import com.example.abhishek.bookshareapp.api.models.LocalBooks.Book;
import com.example.abhishek.bookshareapp.api.models.LocalBooks.BookList;
import com.example.abhishek.bookshareapp.ui.adapter.Local.BooksAdapterSimple;

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
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest);

        rootView = (FrameLayout) findViewById(R.id.root_view);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        adapter = new BooksAdapterSimple(this, booksList, new BooksAdapterSimple.OnItemClickListener() {
            @Override
            public void onItemClick(Book book) {
                Toast.makeText(getApplicationContext(), "Login to get details", Toast.LENGTH_SHORT).show();
            }
        });

        layoutManager = new LinearLayoutManager(this);

        localBookList = (RecyclerView) findViewById(R.id.local_books_list);
        localBookList.setLayoutManager(layoutManager);
        localBookList.setAdapter(adapter);

        final EndlessScrollListener endlessScrollListener = new EndlessScrollListener((LinearLayoutManager) layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                getLocalBooks(String.valueOf(page + 1));
                Toast.makeText(getApplicationContext(), "Loading Page " + (page + 1), Toast.LENGTH_SHORT).show();
            }
        };

        localBookList.addOnScrollListener(endlessScrollListener);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_to_refresh_layout);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_to_refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "onRefresh called from SwipeRefreshLayout ");
                endlessScrollListener.reset();
                getLocalBooks("1");
            }

        });

        getLocalBooks("1");
    }

    public void getLocalBooks(final String page) {
        UsersAPI api = NetworkingFactory.getLocalInstance().getUsersAPI();
        Call<BookList> call = api.getBList(page);
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
                TransitionManager.beginDelayedTransition(rootView);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<BookList> call, Throwable t) {
                Log.d("searchresp", "searchOnFail " + t.toString());
                refreshLayout.setRefreshing(false);
                TransitionManager.beginDelayedTransition(rootView);
                progressBar.setVisibility(View.GONE);
            }
        });

    }

}
