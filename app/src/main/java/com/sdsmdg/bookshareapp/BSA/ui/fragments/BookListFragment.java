package com.sdsmdg.bookshareapp.BSA.ui.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.api.BooksAPI;
import com.sdsmdg.bookshareapp.BSA.api.NetworkingFactory;
import com.sdsmdg.bookshareapp.BSA.api.models.Book;
import com.sdsmdg.bookshareapp.BSA.api.models.GoodreadsResponse;
import com.sdsmdg.bookshareapp.BSA.api.models.Search;
import com.sdsmdg.bookshareapp.BSA.ui.adapter.GR.BooksAdapterGR;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookListFragment extends Fragment {

    public static String resp;

    public static String getResp() {
        return resp;
    }

    private RecyclerView resultsList;
    List<Book> bookList = new ArrayList<>();
    BooksAdapterGR adapter;
    private RecyclerView.LayoutManager mLayoutManager;
    TextView no_books;
    Search sr;
    Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.book_list_fragment, container, false);
        no_books = (TextView) view.findViewById(R.id.no_books);
        no_books.setVisibility(View.GONE);
        resultsList = (RecyclerView) view.findViewById(R.id.results_list);
        resultsList.setNestedScrollingEnabled(false);
        mLayoutManager = new LinearLayoutManager(getActivity());
        resultsList.setLayoutManager(mLayoutManager);
        context = getActivity();

        adapter = new BooksAdapterGR(getActivity(), bookList, new BooksAdapterGR.OnItemClickListener() {
            @Override
            public void onItemClick(Book book) {
            }
        });
        resultsList.setAdapter(adapter);

        return view;
    }

    public void getBooks(String query, String field, String key) {

        BooksAPI api = NetworkingFactory.getGRInstance().getBooksApi();
        Call<GoodreadsResponse> call = api.getBooks(query, field, key);
        call.enqueue(new Callback<GoodreadsResponse>() {
            @Override
            public void onResponse(Call<GoodreadsResponse> call, Response<GoodreadsResponse> response) {
                if (response.body() != null) {
                    sr = response.body().getSearch();
                    bookList.clear();
                    bookList.addAll(sr.getBooks());
                    adapter.notifyDataSetChanged();
                    if (bookList.size() == 0) {
                        no_books.setVisibility(View.VISIBLE);
                    } else {
                        no_books.setVisibility(View.GONE);

                    }
                    resp = response.toString();
                }
            }

            @Override
            public void onFailure(Call<GoodreadsResponse> call, Throwable t) {
                resp = "failed";
            }
        });

    }

}
