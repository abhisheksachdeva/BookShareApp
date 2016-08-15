package com.sdsmdg.bookshareapp.BSA.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
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
    ProgressBar progress;
    LinearLayout l1, l2 ;
    Button dismiss;
    Integer count = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_results);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        scrollingView = (NestedScrollView) findViewById(R.id.scrollView);
        scrollingView.getForeground().setAlpha(0);
        progress = (ProgressBar) findViewById(R.id.progress);
        l1 = (LinearLayout)findViewById(R.id.layoutp1);
        l2 = (LinearLayout)findViewById(R.id.layoutp2);
        l1.setVisibility(View.INVISIBLE);
        l2.setVisibility(View.INVISIBLE);
        progress.setVisibility(View.INVISIBLE);
        dismiss = (Button)findViewById(R.id.dismiss);
        dismiss.setVisibility(View.INVISIBLE);
        dismiss.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                scrollingView.getForeground().setAlpha(0);
                progress.setVisibility(View.GONE);
                l1.setVisibility(View.GONE);
                l2.setVisibility(View.GONE);
            }
        });

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
                scrollingView.getForeground().setAlpha(0);
                progress.setVisibility(View.GONE);
                l1.setVisibility(View.GONE);
                l2.setVisibility(View.GONE);

            } else {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollingView.getForeground().setAlpha(0);
                        progress.setVisibility(View.GONE);
                        l1.setVisibility(View.GONE);
                        l2.setVisibility(View.GONE);
                    }
                }, 1000);

            }
        }

        @Override
        protected void onPreExecute() {

            scrollingView.getForeground().setAlpha(180);
            l1.setVisibility(View.VISIBLE);
            l2.setVisibility(View.VISIBLE);
            progress.setVisibility(View.VISIBLE);
            dismiss.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progress.setProgress(values[0]);
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
