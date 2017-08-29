package com.sdsmdg.bookshareapp.BSA.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.sdsmdg.bookshareapp.BSA.Listeners.EndlessScrollListener;
import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.api.NetworkingFactory;
import com.sdsmdg.bookshareapp.BSA.api.UsersAPI;
import com.sdsmdg.bookshareapp.BSA.api.models.LocalBooks.Book;
import com.sdsmdg.bookshareapp.BSA.api.models.LocalBooks.BookList;
import com.sdsmdg.bookshareapp.BSA.api.models.VerifyToken.Detail;
import com.sdsmdg.bookshareapp.BSA.firebase_classes.MyFirebaseMessagingService;
import com.sdsmdg.bookshareapp.BSA.ui.adapter.Local.BooksAdapterSimple;
import com.sdsmdg.bookshareapp.BSA.ui.fragments.NotificationFragment;
import com.sdsmdg.bookshareapp.BSA.utils.CommonUtilities;
import com.sdsmdg.bookshareapp.BSA.utils.Helper;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener, NotificationFragment.OnFragmentInteractionListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CODE = 1000;
    List<Book> booksList;
    BooksAdapterSimple adapter;
    SharedPreferences prefs;
    SwipeRefreshLayout refreshLayout;
    SearchView searchView;
    Integer count = 1;
    String Resp;

    CustomProgressDialog customProgressDialog;

    //Creates a list of visible snackbars
    List<Snackbar> visibleSnackbars = new ArrayList<>();
    String resplocal;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    RecyclerView localBooksList;
    Toolbar toolbar;
    int backCounter = 0;
    ImageView _profilePicture;
    String url;
    NotificationFragment notifFragment;
    Boolean progress_isVisible = false;
    //Search Menu item reference in the toolbar
    MenuItem searchItem;
    TextView noBookstextview;

    //toReadName for to-read search
    String toReadName = null;

    //Create a realm object to handle our local database
    Realm realm;

    public String getResp() {
        return Resp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Realm.init(this);

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();

        // Create a new empty instance of Realm
        realm = Realm.getInstance(realmConfiguration);

        //customProgressDialog = new CustomProgressDialog(MainActivity.this);
        //customProgressDialog.setCancelable(false);
        prefs = getSharedPreferences("Token", MODE_PRIVATE);

        Helper.setUserId(prefs.getString("id", prefs.getString("id", "")));
        Helper.setUserName(prefs.getString("first_name", null) + " " + prefs.getString("last_name", null));

        Helper.setId(prefs.getString("id", null));
        Helper.setToken(prefs.getString("token", null));

        SharedPreferences preferences = getSharedPreferences("Token", MODE_PRIVATE);

        setContentView(R.layout.activity_main);
        noBookstextview = (TextView) findViewById(R.id.no_books_textView);
        noBookstextview.setVisibility(View.GONE);

        progress_isVisible = false;

        notifFragment = (NotificationFragment) getSupportFragmentManager().findFragmentById(R.id.right_drawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


        if (getIntent().getExtras() != null) {

                toReadName = getIntent().getExtras().getString("toReadName");
                if (toReadName != null) {
                    if (toReadName.equals("open")) {
                        notifFragment.getNotifications("1");
                        MyFirebaseMessagingService.notifications.clear();
                        drawerLayout.openDrawer(GravityCompat.END);
                    }
                }


                toReadName = getIntent().getExtras().getString("data_splash");
                if (toReadName != null && toReadName.equals("open_drawer")) {
                    notifFragment.getNotifications("1");
                    MyFirebaseMessagingService.notifications.clear();
                    drawerLayout.openDrawer(GravityCompat.END);
                }

                toReadName = getIntent().getExtras().getString("data_login");
                if (toReadName != null && toReadName.equals("update")) {

                    String token = "Token " + preferences.getString("token", null);

                    String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                    UsersAPI usersAPI = NetworkingFactory.getLocalInstance().getUsersAPI();
                    Call<Detail> call2 = usersAPI.update_fcm_id(
                            token,
                            refreshedToken
                    );
                    call2.enqueue(new Callback<Detail>() {
                        @Override
                        public void onResponse(Call<Detail> call2, Response<Detail> response) {
                            if (response.body() != null) {
                                if (response.body().getDetail().equals("FCM_ID changed")) {
                                    //FCM Id was changed successfully
                                } else {
//                                Toast.makeText(getApplicationContext(), R.string.connection_failed, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                //The FCM ID didn't change
                            }
                            removeAnyVisibleSnackbars();
                        }

                        @Override
                        public void onFailure(Call<Detail> call, Throwable t) {
                            Snackbar.make(findViewById(R.id.coordinatorlayout), "You are offline", Snackbar.LENGTH_INDEFINITE).setCallback(new Snackbar.Callback() {
                                @Override
                                public void onDismissed(Snackbar snackbar, int event) {
                                    visibleSnackbars.remove(snackbar);
                                    super.onDismissed(snackbar, event);
                                }

                                @Override
                                public void onShown(Snackbar snackbar) {
                                    visibleSnackbars.add(snackbar);
                                    super.onShown(snackbar);
                                }
                            }).setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    refresh();
                                }
                            }).show();
                        }
                    });


                }

        }
        localBooksList = (RecyclerView) findViewById(R.id.localBooksList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        localBooksList.setLayoutManager(layoutManager);

        //Show what's available offline first
        RealmResults<Book> realmBooksList = realm.where(Book.class).findAll();
        booksList = realm.copyFromRealm(realmBooksList);

        adapter = new BooksAdapterSimple(this, booksList, new BooksAdapterSimple.OnItemClickListener() {
            @Override
            public void onItemClick(Book book) {

                if (isOnline()) {
                    Intent intent = new Intent(MainActivity.this, BookDetailsActivity.class);
                    intent.putExtra("id", book.getId());
                    startActivity(intent);
                }

            }
        });
        localBooksList.setAdapter(adapter);

        getLocalBooks("1");

        final EndlessScrollListener endlessScrollListener = new EndlessScrollListener((LinearLayoutManager) layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                getLocalBooks(String.valueOf(page + 1));
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
        String url = CommonUtilities.local_books_api_url + "image/" + Helper.getUserId() + "/";
        this.url = url;
        Picasso.with(this).load(url).memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(R.drawable.ic_profile_pic).into(_profilePicture);
        _profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(MainActivity.this, MyProfile.class);
                startActivity(profileIntent);
            }
        });

        if (_name != null) {
            _name.setText(preferences.getString("first_name", "") + " " + preferences.getString("last_name", ""));
        }

        if (_email != null) {
            _email.setText(Helper.getUserEmail());
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.White));
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                searchItem.setVisible(true);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                searchItem.setVisible(false);
            }
        };
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

        //When a to read book is to be checked if it exists in campus
        toReadName = getIntent().getStringExtra("pass_it_on");

    }

    public void searchClicked(View view) {
        Intent i = new Intent(this, SearchResultsActivity.class);
        startActivityForResult(i, REQUEST_CODE);
    }


    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        UsersAPI api = NetworkingFactory.getLocalInstance().getUsersAPI();
        Call<List<Book>> call = api.search(query, "Token " + prefs.getString("token", null));
        call.enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                booksList.clear();
                if (response.body().size() != 0) {
                    resplocal = response.toString();
                    List<Book> localBooksList = response.body();
                    noBookstextview.setVisibility(View.GONE);
                    booksList.addAll(localBooksList);
                    refreshLayout.setRefreshing(false);
                } else {
                    resplocal = "null";
                    noBookstextview.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();
                removeAnyVisibleSnackbars();
            }

            @Override
            public void onFailure(Call<List<Book>> call, Throwable t) {
                Snackbar.make(findViewById(R.id.coordinatorlayout), "You are offline", Snackbar.LENGTH_INDEFINITE).setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        visibleSnackbars.remove(snackbar);
                        super.onDismissed(snackbar, event);
                    }

                    @Override
                    public void onShown(Snackbar snackbar) {
                        visibleSnackbars.add(snackbar);
                        super.onShown(snackbar);
                    }
                }).setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        refresh();
                    }
                }).show();
                refreshLayout.setRefreshing(false);

            }
        });
        searchView.clearFocus();
        return true;
    }

    /**
     * When an item in the toolbar is clicked, the following
     * method is called.
     **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem notifItem = menu.findItem(R.id.menu_notifs);
        searchItem = menu.findItem(R.id.search);
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
                noBookstextview.setVisibility(View.GONE);

                return true;
            }
        });
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.White));
        searchEditText.setHintTextColor(getResources().getColor(R.color.White));
        searchView.setOnQueryTextListener(this);

        //If toReadName received from to-read is not null, search it first
        if(toReadName != null) {
            searchItem.expandActionView();
            searchView.requestFocus();
            searchView.setQuery(toReadName, true);
        }

        notifItem.setIcon(R.drawable.ic_notif_small);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_notifs) {

            notifFragment.getNotifications("1");
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

        if (id == R.id.nav_myprofile) {
            Intent i = new Intent(this, MyProfile.class);
            startActivity(i);

        } else if (id == R.id.nav_grlogin) {
            SharedPreferences preff = getSharedPreferences("UserId", MODE_PRIVATE);
            if (preff.getString("userGrId", null) == null) {
                Intent in = new Intent(this, GRLoginActivity.class);
                startActivity(in);
            } else {
                Intent i = new Intent(this, ToReadActivity.class);
                startActivity(i);
            }
        } else if (id == R.id.nav_logout) {
            final SharedPreferences prefs = getSharedPreferences("Token", MODE_PRIVATE);
            String token = "Token " + prefs.getString("token", null);

            UsersAPI usersAPI = NetworkingFactory.getLocalInstance().getUsersAPI();
            Call<Detail> call2 = usersAPI.update_fcm_id(
                    token,
                    "none"
            );
            call2.enqueue(new Callback<Detail>() {
                @Override
                public void onResponse(Call<Detail> call2, Response<Detail> response) {
                    if (response.body() != null) {
                        if (response.body().getDetail().equals("FCM_ID changed")) {
                            //The FCM ID was changed successfully
                        } else {
//                            Toast.makeText(getApplicationContext(), R.string.connection_failed, Toast.LENGTH_SHORT).show();
                        }
                        removeAnyVisibleSnackbars();
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.clear();
                        editor.apply();
                        Intent i = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, R.string.connection_failed, Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(i);
                        finish();

                    }
                }

                @Override
                public void onFailure(Call<Detail> call, Throwable t) {
                    Snackbar.make(findViewById(R.id.coordinatorlayout), R.string.you_are_offline, Snackbar.LENGTH_INDEFINITE).setCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            visibleSnackbars.remove(snackbar);
                            super.onDismissed(snackbar, event);
                        }

                        @Override
                        public void onShown(Snackbar snackbar) {
                            visibleSnackbars.add(snackbar);
                            super.onShown(snackbar);
                        }
                    }).setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            refresh();
                        }
                    }).show();
                }
            });


        } else if (id == R.id.nav_share) {

            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");
            String text = "BookShare App !! .You can download the app from here...!";

            waIntent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(waIntent, "Share with"));

        } else if (id == R.id.nav_usersearch) {
            Intent i = new Intent(this, UserSearchActivity.class);
            startActivity(i);
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    public void getLocalBooks(final String page) {
        UsersAPI api = NetworkingFactory.getLocalInstance().getUsersAPI();
        Call<BookList> call = api.getLocalBList(page, "Token " + prefs.getString("token", null));
        call.enqueue(new Callback<BookList>() {
            @Override
            public void onResponse(Call<BookList> call, Response<BookList> response) {
                if (response.body() != null) {
                    Resp = response.toString();
                    List<Book> localBooksList = response.body().getResults();
                    if (page.equals("1")) {

                        adapter.setTotalCount(response.body().getCount());

                        //Save the first page to the offline database
                        realm.beginTransaction();
                        //Remove all previously stored books
                        realm.deleteAll();
                        realm.copyToRealmOrUpdate(localBooksList);
                        realm.commitTransaction();

                        booksList.clear();
                        adapter.notifyDataSetChanged();

                        removeAnyVisibleSnackbars();
                    }
                    booksList.addAll(localBooksList);
                    adapter.notifyDataSetChanged();
                    refreshLayout.setRefreshing(false);
                } else {
                    Resp = "null";
                }

            }

            @Override
            public void onFailure(Call<BookList> call, Throwable t) {
                Snackbar.make(findViewById(R.id.coordinatorlayout), R.string.you_are_offline, Snackbar.LENGTH_INDEFINITE).setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        visibleSnackbars.remove(snackbar);
                        super.onDismissed(snackbar, event);
                    }

                    @Override
                    public void onShown(Snackbar snackbar) {
                        visibleSnackbars.add(snackbar);
                        super.onShown(snackbar);
                    }
                }).setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        refresh();
                    }
                }).show();
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

        if (this.drawerLayout.isDrawerVisible(GravityCompat.START)) {

            this.drawerLayout.closeDrawer(GravityCompat.START);

        } else {
            if (!progress_isVisible) {

                if (backCounter >= 1) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                } else {

                    Snackbar.make(findViewById(R.id.coordinatorlayout), "       Press Again To Exit", Snackbar.LENGTH_LONG).setCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            super.onDismissed(snackbar, event);
                            visibleSnackbars.remove(snackbar);
                            /*
                            This callback is required because if "Press Again To Exit" Snackbar
                            comes in offline mode, the "You are offline" Snackbar should appear
                            again, if user choses not to close the app
                             */
                            if (!isOnline()) {
                                Snackbar.make(findViewById(R.id.coordinatorlayout), "You are offline", Snackbar.LENGTH_INDEFINITE).setCallback(new Snackbar.Callback() {
                                    @Override
                                    public void onDismissed(Snackbar snackbar, int event) {
                                        super.onDismissed(snackbar, event);
                                        visibleSnackbars.remove(snackbar);
                                    }

                                    @Override
                                    public void onShown(Snackbar snackbar) {
                                        super.onShown(snackbar);
                                        visibleSnackbars.add(snackbar);
                                    }
                                }).setAction("RETRY", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        refresh();
                                    }
                                }).show();
                            }
                        }

                        @Override
                        public void onShown(Snackbar snackbar) {
                            super.onShown(snackbar);
                            visibleSnackbars.add(snackbar);
                        }
                    }).show();

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

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Helper.imageChanged) {
            Picasso.with(this).load(url).into(_profilePicture);
            Helper.imageChanged = false;
        }
        navigationView.setCheckedItem(R.id.menu_none);//This will check a invisible item, effectively unselecting all items
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            getLocalBooks("1");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private void removeAnyVisibleSnackbars() {
        if (visibleSnackbars.size() != 0) {
            visibleSnackbars.get(0).dismiss();
            visibleSnackbars.clear();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void refresh() {
        getLocalBooks("1");
    }
}
