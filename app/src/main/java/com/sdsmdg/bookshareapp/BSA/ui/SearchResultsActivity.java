package com.sdsmdg.bookshareapp.BSA.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.ui.fragments.BookListFragment;
import com.sdsmdg.bookshareapp.BSA.utils.CommonUtilities;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {
    String query;
    String API_KEY = CommonUtilities.API_KEY;
    EditText searchEditText;
    BookListFragment bookListFragment;
    NestedScrollView scrollingView;
    FloatingActionButton button;
    Integer count = 0;
    Spinner spinner;//This is used to know whether the search query is author or title, or can be anything
    CustomProgressDialog customProgressDialog;
    String selected = null;
    String isbn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_results);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        customProgressDialog = new CustomProgressDialog(SearchResultsActivity.this);
        customProgressDialog.setCancelable(false);
        scrollingView = (NestedScrollView) findViewById(R.id.scrollView);
        button = (FloatingActionButton) findViewById(R.id.scroll);
        final List<String> searchModeList = new ArrayList<>();
        searchModeList.add("Author");
        searchModeList.add("Title");
        searchModeList.add("All");


        spinner = (Spinner) findViewById(R.id.spinner);

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, searchModeList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected = searchModeList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner.setSelection(2);//Setting the default vaule of spinner to "All"

        scrollingView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > 1000) {
                    button.setVisibility(View.VISIBLE);
                } else {
                    button.setVisibility(View.INVISIBLE);
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollingView.fullScroll(View.FOCUS_UP);

            }
        });

        searchEditText = (EditText) findViewById(R.id.searchEditText);
        bookListFragment = new BookListFragment();

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search(v);
                    return false;
                }
                return false;
            }
        });

        if(getIntent().getExtras()!=null){

        isbn = getIntent().getExtras().getString("isbn");
        searchEditText.setText(isbn.toString());
        bookListFragment.getBooks(isbn, "all", API_KEY);


        }
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, bookListFragment)
                .commit();

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search(v);
                    return true;
                }
                return false;
            }
        });

    }

    class ProgressLoader extends AsyncTask<Integer, Integer, String> {

        @Override
        protected String doInBackground(Integer... params) {

            do {
                try {
                    Thread.sleep(1000);
                    if (bookListFragment.getResp() != null) {
                        break;
                    }
                    publishProgress(count);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (bookListFragment.getResp() != null) {
                    break;
                }
                count++;
            }while (bookListFragment.getResp()==null);


            return "Task Completed.";
        }

        @Override
        protected void onPostExecute(String result) {
            if (bookListFragment.getResp() == null) {
                Toast.makeText(SearchResultsActivity.this, "Please Try Again.", Toast.LENGTH_SHORT).show();
                customProgressDialog.dismiss();


            } else {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        customProgressDialog.dismiss();
                    }
                }, 1000);

            }
        }

        @Override
        protected void onPreExecute() {
            customProgressDialog.show();

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return (true);
        }

        return (super.onOptionsItemSelected(item));
    }



    public void search(View view) {

        hideKeyboard();
        query = searchEditText.getText().toString();
        Log.i("sss",selected.toLowerCase());
        bookListFragment.getBooks(query, selected.toLowerCase(), API_KEY);
        new ProgressLoader().execute();


    }

//    public void searchbyscan(String query_isbn) {
//
//        query = query_isbn;
//        Log.i("sss",selected.toLowerCase());
//        bookListFragment.getBooks(query, "all", API_KEY);
//        new ProgressLoader().execute();
//
//
//    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
//        if(result != null) {
//            if(result.getContents() == null) {
//                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
//            } else {
//                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
//                searchEditText.setText(result.getContents());
//                searchbyscan(result.getContents());
//
//            }
//        } else {
//            super.onActivityResult(requestCode, resultCode, data);
//        }
//    }
}
