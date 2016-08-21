package com.sdsmdg.bookshareapp.BSA.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.ui.fragments.BookListFragment;
import com.sdsmdg.bookshareapp.BSA.utils.CommonUtilities;

public class SearchResultsActivity2 extends AppCompatActivity {
    String query;
    String API_KEY = CommonUtilities.API_KEY;
    EditText searchEditText;
    String mode = "all";
    Spinner select;
    BookListFragment bookListFragment;
    NestedScrollView scrollingView;
    FloatingActionButton button;
    Integer count = 0;
    CustomProgressDialog customProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_results2);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        customProgressDialog = new CustomProgressDialog(SearchResultsActivity2.this);
        customProgressDialog.setCancelable(false);
        scrollingView = (NestedScrollView) findViewById(R.id.scrollView);
        button = (FloatingActionButton) findViewById(R.id.scroll);
        select = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        select.setAdapter(adapter);
        select.setSelection(0);
        select.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).toString()=="All"){
                    mode ="all";
                }else if(parent.getItemAtPosition(position).toString()=="Title"){
                    mode ="title";
                }else {
                    mode = "author";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mode = "all";
            }
        });

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
                    return true;
                }
                return false;
            }
        });
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container2, bookListFragment)
                      .commit();

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
                Toast.makeText(SearchResultsActivity2.this, "Please Try Again.", Toast.LENGTH_SHORT).show();
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
            customProgressDialog.getWindow().setLayout(464, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);

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
        bookListFragment.getBooks(query, mode, API_KEY);
        new ProgressLoader().execute();


    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
