package com.sdsmdg.bookshareapp.BSA.ui.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.abhishek.bookshareapp.R;
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

    private RecyclerView resultsList;
    List<Book> bookList = new ArrayList<>();
    BooksAdapterGR adapter;
    private RecyclerView.LayoutManager mLayoutManager;
    Search sr;
    Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.book_list_fragment, container, false);

        resultsList = (RecyclerView) view.findViewById(R.id.results_list);
        resultsList.setNestedScrollingEnabled(false);
        mLayoutManager = new LinearLayoutManager(getActivity());
        resultsList.setLayoutManager(mLayoutManager);
        context = getActivity();

        adapter = new BooksAdapterGR(getActivity(), bookList, new BooksAdapterGR.OnItemClickListener() {
            @Override
            public void onItemClick(Book book) {
                Toast.makeText(context, book.getBookDetails().getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
        resultsList.setAdapter(adapter);

        return view;
    }

    public void getBooks(String query, String field, String key) {

        Toast.makeText(getActivity(), "Searching...", Toast.LENGTH_SHORT).show();

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
                }
            }

            @Override
            public void onFailure(Call<GoodreadsResponse> call, Throwable t) {
                Log.d("searchresp", "searchOnFail " + t.toString());
            }
        });

    }

}
