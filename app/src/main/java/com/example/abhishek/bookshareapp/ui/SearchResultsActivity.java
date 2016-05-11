package com.example.abhishek.bookshareapp.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.example.abhishek.bookshareapp.R;
import com.example.abhishek.bookshareapp.api.models.Book;
import com.example.abhishek.bookshareapp.api.models.Search;
import com.example.abhishek.bookshareapp.ui.adapter.BooksAdapter;
import com.example.abhishek.bookshareapp.ui.fragments.BookListFragment;
import com.example.abhishek.bookshareapp.utils.CommonUtilities;

import java.util.List;

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
    EditText searchEditText;
    BookListFragment bookListFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_results);

        searchEditText = (EditText) findViewById(R.id.searchEditText);

        bookListFragment = new BookListFragment();

        getFragmentManager().beginTransaction()
                .replace(R.id.container, bookListFragment)
                .commit();

    }

    public void search(View view){
        query = searchEditText.getText().toString();
        bookListFragment.getBooks(query, API_KEY);
    }

}

