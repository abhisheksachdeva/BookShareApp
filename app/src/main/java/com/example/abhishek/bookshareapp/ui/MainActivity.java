package com.example.abhishek.bookshareapp.ui;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhishek.bookshareapp.R;
import com.example.abhishek.bookshareapp.api.NetworkingFactory;
import com.example.abhishek.bookshareapp.api.UsersAPI;
import com.example.abhishek.bookshareapp.api.models.LocalBooks.Book;
import com.example.abhishek.bookshareapp.api.models.Notification.Notifications;
import com.example.abhishek.bookshareapp.ui.adapter.MainScreenBooksAdapter;
import com.example.abhishek.bookshareapp.utils.Helper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static  Integer count =0;
    List<Book> booksList;
    MainScreenBooksAdapter adapter;
    SharedPreferences prefs;
    SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.button);
        RecyclerView localBooksList = (RecyclerView) findViewById(R.id.localBooksList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        localBooksList.setLayoutManager(layoutManager);
        booksList = new ArrayList<>();
        adapter = new MainScreenBooksAdapter(this, booksList, new MainScreenBooksAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Book book) {
                Intent intent = new Intent(getApplicationContext(), BookDetailsActivity.class);
                intent.putExtra("id", book.getId());
                Log.i(TAG, "onItemClick");
                startActivity(intent);
            }
        });
        localBooksList.setAdapter(adapter);
        prefs = getSharedPreferences("Token", MODE_PRIVATE);

        getLocalBooks();
        getNotifications();
        Helper.setUserId(prefs.getString("id", prefs.getString("id", "")));
        Helper.setUserName(prefs.getString("first_name", null) + " " + prefs.getString("last_name", null));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SearchResultsActivity.class);
                startActivity(i);
            }
        });


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View header = navigationView.getHeaderView(0);
        TextView _name = (TextView) header.findViewById(R.id.nav_name);
        TextView _email = (TextView) header.findViewById(R.id.nav_email);

        SharedPreferences preferences = getSharedPreferences("Token", MODE_PRIVATE);

        if (_name != null) {
            _name.setText(preferences.getString("first_name", "") + " " + preferences.getString("last_name", ""));
        }
        if (_email != null) {
            _email.setText(Helper.getUserEmail());
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        refreshLayout =(SwipeRefreshLayout)findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "onRefresh called from SwipeRefreshLayout ");
                getLocalBooks();
                refresh();
                Toast.makeText(MainActivity.this,"Refresh!",Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.menu_notifs);
        if(Helper.getNew_total()>Helper.getOld_total()) {
            item.setIcon(R.drawable.ic_menu_send2);
        }else{
            item.setIcon(R.drawable.ic_menu_send);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.menu_notifs) {
            item.setIcon(R.drawable.ic_menu_send);
            Helper.setOld_total(Helper.getNew_total());
            Intent i = new Intent(this, NotificationActivity.class);
            startActivity(i);
            finish();
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
            finish();

        } else if (id == R.id.nav_myprofile) {
            Intent i = new Intent(this, UserProfile.class);
            i.putExtra("id", prefs.getString("id", prefs.getString("id", "")));
            startActivity(i);

        }  else if (id == R.id.nav_logout) {
            SharedPreferences prefs = getSharedPreferences("Token", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();

        } else if (id == R.id.nav_notifications){
            Intent i = new Intent(this, NotificationActivity.class);
            startActivity(i);

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
                if (response.body() != null) {
                    Log.d("Search Response:", response.toString());
                    List<Book> localBooksList = response.body();
                    booksList.clear();
                    booksList.addAll(localBooksList);
                    adapter.notifyDataSetChanged();
                    refreshLayout.setRefreshing(false);
                }

            }

            @Override
            public void onFailure(Call<List<Book>> call, Throwable t) {
                Log.d("searchresp", "searchOnFail " + t.toString());
                refreshLayout.setRefreshing(false);

            }
        });

    }

    public void getNotifications() {
        UsersAPI usersAPI = NetworkingFactory.getLocalInstance().getUsersAPI();
        Call<List<Notifications>> call = usersAPI.getNotifs(Helper.getUserId());
        call.enqueue(new Callback<List<Notifications>>() {
            @Override
            public void onResponse(Call<List<Notifications>> call, Response<List<Notifications>> response) {
                if (response.body() != null) {
                    List<Notifications> notifList = response.body();
                    Helper.setNew_total(notifList.size());
                    Log.i("NTA","adapter attached");
                    Log.i("Old Total",Helper.getOld_total().toString());
                    Log.i("New Total",Helper.getNew_total().toString());
                }
            }

            @Override
            public void onFailure(Call<List<Notifications>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Check your internet connection and try again!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void refresh() {
        UsersAPI usersAPI = NetworkingFactory.getLocalInstance().getUsersAPI();
        Call<List<Notifications>> call = usersAPI.getNotifs(prefs.getString("id", null));
        call.enqueue(new Callback<List<Notifications>>() {
            @Override
            public void onResponse(Call<List<Notifications>> call, Response<List<Notifications>> response) {
                if (response.body() != null) {
                    List<Notifications> notifList = response.body();

                    for (int i = 0; i < notifList.size(); i++) {

                        Log.i("Notif Loader", "" + notifList.size());
                        String content = "";
                        Notifications notifications = notifList.get(i);
                        if (notifications.getMessage().equals("requested for")) {
                            content = notifications.getSenderName() + " requested for " + notifications.getBookTitle() + "\n";
                        } else if (notifications.getMessage().equals("You rejected request for")) {
                            if (!notifications.getSenderId().equals(prefs.getString("id", null))) {
                                content = notifications.getSenderName() + " rejected your request for " + notifications.getBookTitle();
                            }
                        } else if (notifications.getMessage().equals("has accepted your request for")) {
                            content = notifications.getSenderName() + " " + notifications.getMessage() + " " + notifications.getBookTitle() + "\n";
                        }

                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(getApplicationContext())
                                        .setSmallIcon(R.drawable.default_profile_pic)
                                        .setContentTitle("BookShareApp")
                                        .setContentText(content)
                                        .setAutoCancel(true);

                        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle()
                                .setBigContentTitle("BookShareApp")
                                .bigText(content);

                        mBuilder.setStyle(bigTextStyle);

                        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);

                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
                        stackBuilder.addParentStack(MainActivity.class);
                        stackBuilder.addNextIntent(resultIntent);
                        PendingIntent resultPendingIntent =
                                stackBuilder.getPendingIntent(
                                        0,
                                        PendingIntent.FLAG_UPDATE_CURRENT
                                );
                        mBuilder.setContentIntent(resultPendingIntent);
                        NotificationManager mNotificationManager =
                                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager.notify(1, mBuilder.build());
                    }


                } else
                    Log.i("harshit", "List.size() == 0");

            }

            @Override
            public void onFailure(Call<List<Notifications>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Check your network connectivity and try again!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
