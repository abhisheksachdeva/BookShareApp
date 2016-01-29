package com.example.abhishek.bookshareapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.SortedList;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    ListView listview;
    //List<Book> books;
    public static final String url="https://www.googleapis.com/books/v1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listview= (ListView)findViewById(R.id.listview);

        getBooks();
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(url)
//                .build();
//
//        BooksAPI api = retrofit.create(BooksAPI.class);





    }

    public void getBooks(){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .build();

        BooksAPI api = retrofit.create(BooksAPI.class);

        api.getBooks(new Callback<List<Book>>() {
            @Override
            public void onResponse(retrofit2.Response<List<Book>> response) {


            }

            @Override
            public void onFailure(Throwable t) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
