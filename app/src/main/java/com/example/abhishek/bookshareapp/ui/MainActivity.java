package com.example.abhishek.bookshareapp.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.abhishek.bookshareapp.R;
import com.example.abhishek.bookshareapp.api.NetworkingFactory;
import com.example.abhishek.bookshareapp.api.UsersAPI;
import com.example.abhishek.bookshareapp.api.models.LocalBooks.Book;
import com.example.abhishek.bookshareapp.ui.adapter.LocalBooksAdapter;
import com.example.abhishek.bookshareapp.ui.adapter.MainScreenBooksAdapter;
import com.example.abhishek.bookshareapp.utils.Helper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    public static final String TAG = MainActivity.class.getSimpleName();

    ListView listview;
    List<Book> booksList;
    String query;
    MainScreenBooksAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton button= (FloatingActionButton) findViewById(R.id.button);

        RecyclerView localBooksList = (RecyclerView) findViewById(R.id.localBooksList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        localBooksList.setLayoutManager(layoutManager);
        booksList = new ArrayList<>();
        adapter = new MainScreenBooksAdapter(this, booksList, new MainScreenBooksAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Book book) {
                Intent intent = new Intent(getApplicationContext(), BookDetailsActivity.class);
                intent.putExtra("id",book.getId());
                Log.i(TAG, "onItemClick");
                startActivity(intent);
            }
        });
        localBooksList.setAdapter(adapter);

        getLocalBooks();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(MainActivity.this,SearchResultsActivity.class);
                startActivity(i);
            }
        });


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View header=navigationView.getHeaderView(0);
        TextView _name = (TextView) header.findViewById(R.id.nav_name);
        TextView _email = (TextView) header.findViewById(R.id.nav_email);

        if (_name != null) {
            _name.setText("Default UserName");
        }
        if (_email != null) {
            _email.setText(Helper.getUserEmail());
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

    }



    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_mybooks) {
            Intent i = new Intent(this, MyBooks.class);
            startActivity(i);
            finish();

        } else if (id == R.id.nav_myprofile) {
            Intent i = new Intent(this, UserProfile.class);
            startActivity(i);
            finish();

        } else if (id == R.id.nav_change_password) {

        } else if (id == R.id.nav_logout) {
            SharedPreferences prefs = getSharedPreferences("Token", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.commit();

            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();

        } else if (id == R.id.nav_share) {

            PackageManager pm=getPackageManager();
            try {

                Intent waIntent = new Intent(Intent.ACTION_SEND);
                waIntent.setType("text/plain");
                String text= "BookShare App !! .You can download the app from here...!";
                ;

                PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                //Check if package exists or not. If not then code
                //in catch block will be called
                waIntent.setPackage("com.whatsapp");

                waIntent.putExtra(Intent.EXTRA_TEXT, text);
                startActivity(Intent.createChooser(waIntent, "Share with"));

            } catch (PackageManager.NameNotFoundException e) {

            }

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void getLocalBooks() {

        UsersAPI api = NetworkingFactory.getLocalInstance().getUsersAPI();
        Call<List<Book>> call = api.getBooksList();
        call.enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                if(response.body()!=null) {
                    Log.d("Search Response:", response.toString());
                    List<com.example.abhishek.bookshareapp.api.models.LocalBooks.Book> localBooksList = response.body();
                    booksList.clear();
                    booksList.addAll(localBooksList);

                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onFailure(Call<List<Book>> call, Throwable t) {
                Log.d("searchresp","searchOnFail "+ t.toString());
            }
        });

    }

}
