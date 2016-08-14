package com.sdsmdg.bookshareapp.BSA.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sdsmdg.bookshareapp.BSA.Listeners.EndlessScrollListener;
import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.api.NetworkingFactory;
import com.sdsmdg.bookshareapp.BSA.api.UsersAPI;
import com.sdsmdg.bookshareapp.BSA.api.models.LocalBooks.Book;
import com.sdsmdg.bookshareapp.BSA.api.models.LocalBooks.BookList;
import com.sdsmdg.bookshareapp.BSA.ui.adapter.Local.BooksAdapterSimple;
import com.sdsmdg.bookshareapp.BSA.ui.fragments.NotificationFragment;
import com.sdsmdg.bookshareapp.BSA.utils.CommonUtilities;
import com.sdsmdg.bookshareapp.BSA.utils.Helper;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener, NotificationFragment.OnFragmentInteractionListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    List<Book> booksList;
    BooksAdapterSimple adapter;
    SharedPreferences prefs;
    SwipeRefreshLayout refreshLayout;
    SearchView searchView;
    Integer count = 1;
    ProgressDialog progress;
    String Resp;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    RecyclerView localBooksList;
    Toolbar toolbar;
    int backCounter=0;
    ImageView _profilePicture;
    String url;
    NotificationFragment notifFragment;
    ProgressBar progressBar;
    LinearLayout linearLayout1,linearLayout2;
    FrameLayout frameLayout;
    Button dismiss;
    Boolean progress_isVisible = false;

    public String getResp() {
        return Resp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences("Token", MODE_PRIVATE);

        Helper.setUserId(prefs.getString("id", prefs.getString("id", "")));
        Helper.setUserName(prefs.getString("first_name", null) + " " + prefs.getString("last_name", null));

        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar)findViewById(R.id.progress);
        linearLayout1 = (LinearLayout) findViewById(R.id.layoutp1) ;
        linearLayout2 = (LinearLayout) findViewById(R.id.layoutp2) ;
        frameLayout = (FrameLayout) findViewById(R.id.mainframelyout);
        frameLayout.getForeground().setAlpha(180);
        dismiss = (Button)findViewById(R.id.dismiss);
        dismiss.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                frameLayout.getForeground().setAlpha(0);
                progressBar.setVisibility(View.GONE);
                linearLayout2.setVisibility(View.GONE);
                linearLayout1.setVisibility(View.GONE);
                progress_isVisible= false;
            }
        });


        new ProgressLoader().execute( );

        notifFragment = (NotificationFragment)getSupportFragmentManager().findFragmentById(R.id.right_drawer);

        localBooksList = (RecyclerView) findViewById(R.id.localBooksList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        localBooksList.setLayoutManager(layoutManager);
        booksList = new ArrayList<>();
        adapter = new BooksAdapterSimple(this, booksList, new BooksAdapterSimple.OnItemClickListener() {
            @Override
            public void onItemClick(Book book) {

                if(isOnline()){
                Intent intent = new Intent(MainActivity.this,BookDetailsActivity.class);
                intent.putExtra("id", book.getId());
                startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(),"Not connected to Internet", Toast.LENGTH_SHORT).show();
                }

            }
        });
        localBooksList.setAdapter(adapter);

        getLocalBooks("1");

        final EndlessScrollListener endlessScrollListener = new EndlessScrollListener((LinearLayoutManager) layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                getLocalBooks(String.valueOf(page + 1));
                Toast.makeText(getApplicationContext(), "Loading Page " + (page + 1), Toast.LENGTH_SHORT).show();
            }
        };

        localBooksList.addOnScrollListener(endlessScrollListener);
        navigationView = (NavigationView) findViewById(R.id.left_drawer);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        TextView _name = (TextView) header.findViewById(R.id.nav_name);
        TextView _email = (TextView) header.findViewById(R.id.nav_email);
        ImageView _profilePicture = (ImageView) header.findViewById(R.id.nav_profile_picture);
        this._profilePicture = _profilePicture;
        String url = CommonUtilities.local_books_api_url+"image/"+Helper.getUserId()+"/";
        this.url = url;
        Picasso.with(this).load(url).memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(R.drawable.ic_account_circle_black_24dp).into(_profilePicture);

        SharedPreferences preferences = getSharedPreferences("Token", MODE_PRIVATE);

        if (_name != null) {
            _name.setText(preferences.getString("first_name", "") + " " + preferences.getString("last_name", ""));
        }

        if (_email != null) {
            _email.setText(Helper.getUserEmail());
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);

        toggle.syncState();

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                endlessScrollListener.reset();
                getLocalBooks("1");
            }

        });
    }

    public void searchClicked(View view) {
        Intent i = new Intent(this, SearchResultsActivity.class);
        startActivity(i);
    }

    class ProgressLoader extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {

            do {
                try {
                    Thread.sleep(1000);
                    if (getResp() != null) {
                        break;
                    }
                    publishProgress(count);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (getResp() != null) {
                    break;
                }
                count++;
            }while (getResp()==null);


            return "Task Completed.";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress_isVisible= true;

        }

        @Override
        protected void onPostExecute(String result) {
            if (getResp() == null) {
                Toast.makeText(MainActivity.this, "Please Try Again.", Toast.LENGTH_SHORT).show();
                linearLayout1.setVisibility(View.GONE);
                linearLayout2.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                progress_isVisible= false;


            } else {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        frameLayout.getForeground().setAlpha(0);
                        progressBar.setVisibility(View.GONE);
                        linearLayout1.setVisibility(View.GONE);
                        linearLayout2.setVisibility(View.GONE);
                        progress_isVisible= false;
                    }
                }, 1000);

            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progress.setProgress(values[0]);
        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        UsersAPI api = NetworkingFactory.getLocalInstance().getUsersAPI();
        Call<List<Book>> call = api.search(query);
        call.enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                if (response.body() != null) {
                    List<Book> localBooksList = response.body();
                    booksList.clear();
                    booksList.addAll(localBooksList);
                    adapter.notifyDataSetChanged();
                    refreshLayout.setRefreshing(false);
                }

            }

            @Override
            public void onFailure(Call<List<Book>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Check your internet connectivity and try again!", Toast.LENGTH_SHORT).show();
                refreshLayout.setRefreshing(false);

            }
        });
        searchView.clearFocus();
        return true;
    }

    /* When an item in the toolbar is clicked, the following
     * method is called.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem notifItem = menu.findItem(R.id.menu_notifs);
        final MenuItem searchItem = menu.findItem(R.id.search);
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                notifItem.setVisible(false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                notifItem.setVisible(true);
                getLocalBooks("1");
                return true;
            }
        });
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        notifItem.setIcon(R.drawable.ic_notifications_none_white_24dp);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_notifs) {
            notifFragment.getNotifications();
            Helper.setOld_total(Helper.getNew_total());
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END);
            } else {
                drawerLayout.closeDrawer(GravityCompat.START);
                drawerLayout.openDrawer(GravityCompat.END);

            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_mybooks) {
            Intent i = new Intent(this, MyBooks.class);
            startActivity(i);

        } else if (id == R.id.nav_myprofile) {
            Intent i = new Intent(this, MyProfile.class);
            i.putExtra("id", prefs.getString("id", prefs.getString("id", "")));
            startActivity(i);

        } else if (id == R.id.nav_logout) {
            SharedPreferences prefs = getSharedPreferences("Token", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();

        } else if (id == R.id.nav_share) {

            PackageManager pm = getPackageManager();
            try {

                Intent waIntent = new Intent(Intent.ACTION_SEND);
                waIntent.setType("text/plain");
                String text = "BookShare App !! .You can download the app from here...!";

                PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                //Check if package exists or not. If not then code
                //in catch block will be called
                waIntent.setPackage("com.whatsapp");

                waIntent.putExtra(Intent.EXTRA_TEXT, text);
                startActivity(Intent.createChooser(waIntent, "Share with"));

            } catch (PackageManager.NameNotFoundException e) {
                //who cares
            }

        } else if(id == R.id.nav_usersearch) {
            Intent i = new Intent(this, UserSearchActivity.class);
            startActivity(i);
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    public void getLocalBooks(final String page) {
        UsersAPI api = NetworkingFactory.getLocalInstance().getUsersAPI();
        Call<BookList> call = api.getBList(page);
        call.enqueue(new Callback<BookList>() {
            @Override
            public void onResponse(Call<BookList> call, Response<BookList> response) {
                if (response.body() != null) {
                    Resp = response.toString();
                    List<Book> localBooksList = response.body().getResults();
                    if (page.equals("1")) {
                        booksList.clear();
                        adapter.notifyDataSetChanged();
                    }
                    booksList.addAll(localBooksList);
                    adapter.notifyDataSetChanged();
                    refreshLayout.setRefreshing(false);
                }

            }

            @Override
            public void onFailure(Call<BookList> call, Throwable t) {
                Log.d("MA_SearchResponse", "searchOnFail " + t.toString());
                refreshLayout.setRefreshing(false);

            }
        });

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onBackPressed() {

        if(!progress_isVisible) {


            if (backCounter >= 1) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Toast.makeText(this, "Ciao Buddy !", Toast.LENGTH_SHORT).show();
                startActivity(intent);

            } else {
                Snackbar.make(findViewById(R.id.drawer_layout), "       Press Again To Exit", Snackbar.LENGTH_LONG).show();
                backCounter++;
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        backCounter = 0;
                    }
                }, 2000);

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Helper.imageChanged){
            Picasso.with(this).load(url).into(_profilePicture);
            Helper.imageChanged = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
