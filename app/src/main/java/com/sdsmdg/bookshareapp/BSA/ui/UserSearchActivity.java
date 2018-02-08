package com.sdsmdg.bookshareapp.BSA.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.api.NetworkingFactory;
import com.sdsmdg.bookshareapp.BSA.api.UsersAPI;
import com.sdsmdg.bookshareapp.BSA.api.models.LocalUsers.UserInfo;
import com.sdsmdg.bookshareapp.BSA.ui.adapter.Local.UsersAdapter;
import com.sdsmdg.bookshareapp.BSA.utils.Helper;
import com.sdsmdg.bookshareapp.BSA.utils.RxSearchObservable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserSearchActivity extends ActionBarActivity {

    private MenuItem mSearchAction;
    private boolean isSearchOpened = false;
    private EditText edtSeach;

    RecyclerView usersRecyclerView;
    List<UserInfo> userInfoList = new ArrayList<>();
    SharedPreferences preferences;
    EditText queryEditText;
    UsersAdapter adapter;
    TextView noUsersTextView;
    CustomProgressDialog customProgressDialog;
    private Toolbar mToolbar;
    private ProgressBar progressBar;

    private final String TAG = UserSearchActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mToolbar.setClickable(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        customProgressDialog = new CustomProgressDialog(UserSearchActivity.this);
        customProgressDialog.setCancelable(false);

        noUsersTextView = (TextView) findViewById(R.id.no_users_textView);
        progressBar = (ProgressBar) findViewById(R.id.indeterminateBar);
        preferences = getSharedPreferences("Token", MODE_PRIVATE);

        search_open();
        usersRecyclerView = (RecyclerView) findViewById(R.id.user_list);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new UsersAdapter(false, Helper.getUserId(), this, userInfoList, null, null, new UsersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(UserInfo userInfo) {
            }
        });
        usersRecyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_user_search, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mSearchAction = menu.findItem(R.id.action_search);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return (true);

            case R.id.action_settings:
                return true;
            case R.id.action_search:
//                handleMenuSearch();
                return true;


        }
        return (super.onOptionsItemSelected(item));
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private Observable<List<UserInfo>> dataFromNetwork(final String query) {
        UsersAPI api = NetworkingFactory.getLocalInstance().getUsersAPI();
        SharedPreferences preferences = getSharedPreferences("Token", MODE_PRIVATE);
        return api.searchUser
                (query, "Token " + preferences.getString("token", null))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public void onBackPressed() {
        if (isSearchOpened) {
            handleMenuSearch();
            return;
        }
        super.onBackPressed();
    }

    protected void handleMenuSearch() {
        ActionBar action = getSupportActionBar(); //get the actionbar

        if (isSearchOpened) { //test if the search is open
            action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
            action.setDisplayShowTitleEnabled(true); //show the title in the action bar

            //hides the keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edtSeach.getWindowToken(), 0);

            //add the search icon in the action bar
//            mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_search));

            isSearchOpened = false;
        } else { //open the search entry

            action.setDisplayShowCustomEnabled(true); //enable it to display a
            // custom view in the action bar.
            action.setCustomView(R.layout.user_search_bar);//add the custom view
            action.setDisplayShowTitleEnabled(false); //hide the title

            edtSeach = (EditText) action.getCustomView().findViewById(R.id.edtSearch); //the text editor
            edtSeach.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            //this is a listener to do a search when the user clicks on search button
            edtSeach.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    edtSeach.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(edtSeach, InputMethodManager.SHOW_IMPLICIT);
                }
            });

            edtSeach.requestFocus();

            //open the keyboard focused in the edtSearch
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edtSeach, InputMethodManager.SHOW_IMPLICIT);

            //add the close icon
            mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_cross));
            isSearchOpened = true;
        }
    }

    private void search_open() {
        ActionBar action = getSupportActionBar(); //get the actionbar

        action.setDisplayShowCustomEnabled(true); //enable it to display a
        // custom view in the action bar.
        action.setCustomView(R.layout.user_search_bar);//add the custom view
        action.setDisplayShowTitleEnabled(false); //hide the title
        edtSeach = (EditText) action.getCustomView().findViewById(R.id.edtSearch); //the text editor
        edtSeach.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        edtSeach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtSeach.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(edtSeach, InputMethodManager.SHOW_IMPLICIT);

            }
        });

        edtSeach.requestFocus();

        //open the keyboard focused in the edtSearch
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edtSeach, InputMethodManager.SHOW_IMPLICIT);
        //add the close icon

        regRxObservable();
    }

    private void regRxObservable() {
        InputMethodManager imm = (InputMethodManager) getApplication().getSystemService(Context.INPUT_METHOD_SERVICE);
        RxSearchObservable.imm = imm;
        RxSearchObservable.fromView(edtSeach)
                .debounce(300, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        showProgressBar();
                    }
                })
                .switchMap(new Function<String, ObservableSource<List<UserInfo>>>() {
                    @Override
                    public ObservableSource<List<UserInfo>> apply(String query) throws Exception {
                        return dataFromNetwork(query);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<UserInfo>>() {
                    @Override
                    public void accept(List<UserInfo> userInfos) throws Exception {
                        hideProgressBar();
                        showResults(userInfos);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        hideProgressBar();
                        Toast.makeText(getApplicationContext(), R.string.connection_failed, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showResults(List<UserInfo> userInfos) {
        userInfoList.clear();
        userInfoList.addAll(userInfos);
        if (userInfoList.size() == 0){
            noUsersTextView.setVisibility(View.VISIBLE);
            usersRecyclerView.setVisibility(View.GONE);
        }else{
            noUsersTextView.setVisibility(View.GONE);
            usersRecyclerView.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        noUsersTextView.setVisibility(View.GONE);
        usersRecyclerView.setVisibility(View.GONE);
    }
}
