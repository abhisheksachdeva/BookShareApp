package com.example.abhishek.bookshareapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.abhishek.bookshareapp.R;
import com.example.abhishek.bookshareapp.api.models.VolumeInfo;
import com.example.abhishek.bookshareapp.api.BooksAPI;
import com.example.abhishek.bookshareapp.api.models.Book;
import com.example.abhishek.bookshareapp.api.models.BookResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

import static com.example.abhishek.bookshareapp.utils.CommonUtilities.google_api_url;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    ListView listview;
    List<Book> books;
    String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText search=(EditText)findViewById(R.id.search);
        Button button= (Button)findViewById(R.id.button);
        query= "isbn:";

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(MainActivity.this,SearchResultsActivity.class);
                query+=search.getText();
                Log.d("querymain",query);
                i.putExtra("query",query);
                startActivity(i);
            }
        });

        String isbn= "9780553819229";
        listview= (ListView)findViewById(R.id.listview);

        //getBooks("isbn:"+isbn);





    }

    public void getBooks(String query){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(google_api_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BooksAPI api = retrofit.create(BooksAPI.class);

        Call<BookResponse> call = api.getBooks(query);
        call.enqueue(new Callback<BookResponse>() {
            @Override
            public void onResponse(retrofit2.Response<BookResponse> response) {
                List<Book> list = response.body().getItems();

                Log.i(TAG, list.toString());
                Log.i("list", String.valueOf(list.size()));
                Toast.makeText(MainActivity.this, String.valueOf(list.size()), Toast.LENGTH_SHORT).show();
                Book bk = list.get(0);
                String id = bk.getId();
                Toast.makeText(MainActivity.this, id, Toast.LENGTH_SHORT).show();
                VolumeInfo vinfo1 = bk.getInfo();
                try {
                    Log.d("vinfo", vinfo1.getTitle());
                } catch (Exception e) {
                    Log.d("abcd", e.toString());
                }
                // Toast.makeText(MainActivity.this,vi.get(0).getTitle(),Toast.LENGTH_SHORT).show();
                Log.i("resp", id);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i("fail", "no resp");
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
