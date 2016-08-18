package com.sdsmdg.bookshareapp.BSA.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.api.NetworkingFactory;
import com.sdsmdg.bookshareapp.BSA.api.UsersAPI;
import com.sdsmdg.bookshareapp.BSA.api.models.UserInfo;
import com.sdsmdg.bookshareapp.BSA.ui.adapter.Local.UsersAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserSearchActivity extends AppCompatActivity {

    RecyclerView usersRecyclerView;
    List<UserInfo> userInfoList = new ArrayList<>();
    SharedPreferences preferences;
    EditText queryEditText;
    UsersAdapter adapter;
    TextView noUsersTextView;
    CustomProgressDialog customProgressDialog;


    private final String TAG = UserSearchActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);

        customProgressDialog = new CustomProgressDialog(UserSearchActivity.this);
        customProgressDialog.setCancelable(false);

        noUsersTextView = (TextView) findViewById(R.id.no_users_textView);

        preferences = getSharedPreferences("Token", MODE_PRIVATE);

        queryEditText = (EditText) findViewById(R.id.edit_query);

        usersRecyclerView = (RecyclerView) findViewById(R.id.user_list);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new UsersAdapter(null, this, userInfoList, null, null, new UsersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(UserInfo userInfo) {
                Log.i(TAG, "onItemClick: " + userInfo.getFirstName());
            }
        });

        usersRecyclerView.setAdapter(adapter);

    }

    public void userSearchClicked(View view) {
        hideKeyboard();
        noUsersTextView.setVisibility(View.GONE);
        customProgressDialog.show();
        customProgressDialog.getWindow().setLayout(464,LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        UsersAPI api = NetworkingFactory.getLocalInstance().getUsersAPI();
        Call<List<UserInfo>> call = api.searchUser(queryEditText.getText().toString());
        call.enqueue(new Callback<List<UserInfo>>() {
            @Override
            public void onResponse(Call<List<UserInfo>> call, Response<List<UserInfo>> response) {
                userInfoList.clear();
                if (response.body().size() != 0) {
                    userInfoList.addAll(response.body());
                } else {
                    noUsersTextView.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        customProgressDialog.dismiss();
                    }
                }, 1000);
            }

            @Override
            public void onFailure(Call<List<UserInfo>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Check your internet connectivity and try again", Toast.LENGTH_SHORT).show();
                customProgressDialog.dismiss();
            }
        });
    }
    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
