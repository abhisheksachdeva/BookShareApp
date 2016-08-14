package com.sdsmdg.bookshareapp.BSA.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

    private final String TAG = UserSearchActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);

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
        UsersAPI api = NetworkingFactory.getLocalInstance().getUsersAPI();
        Call<List<UserInfo>> call = api.searchUser(queryEditText.getText().toString());
        call.enqueue(new Callback<List<UserInfo>>() {
            @Override
            public void onResponse(Call<List<UserInfo>> call, Response<List<UserInfo>> response) {
                Log.i(TAG, "onResponse: " + response.body().get(0).getEmail());
                userInfoList.clear();
                userInfoList.addAll(response.body());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<UserInfo>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Check your internet connectivity and try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
