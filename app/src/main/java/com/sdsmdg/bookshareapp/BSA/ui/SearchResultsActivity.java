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
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.ui.fragments.BookListFragment;
import com.sdsmdg.bookshareapp.BSA.utils.CommonUtilities;

public class SearchResultsActivity extends AppCompatActivity {
    String query;
    String API_KEY = CommonUtilities.API_KEY;
    EditText searchEditText;
    String mode = "all";
    RadioButton r1, r2, r3;
    BookListFragment bookListFragment;
    NestedScrollView scrollingView;
    FloatingActionButton button;
    Integer count = 0;
    CustomProgressDialog customProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_results);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        customProgressDialog = new CustomProgressDialog(SearchResultsActivity.this);
        customProgressDialog.setCancelable(false);
        scrollingView = (NestedScrollView) findViewById(R.id.scrollView);
        button = (FloatingActionButton) findViewById(R.id.scroll);

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
        r1 = (RadioButton) findViewById(R.id.all);
        r2 = (RadioButton) findViewById(R.id.title);
        r3 = (RadioButton) findViewById(R.id.author);
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
                .replace(R.id.container, bookListFragment)
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

        if (r1.isChecked()) {
            mode = "all";
        }
        if (r2.isChecked()) {
            mode = "title";
        } else if (r3.isChecked()) {
            mode = "author";
        }

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
