package com.example.abhishek.bookshareapp.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.abhishek.bookshareapp.R;
import com.example.abhishek.bookshareapp.api.NetworkingFactory;
import com.example.abhishek.bookshareapp.api.UsersAPI;
import com.example.abhishek.bookshareapp.api.models.UserInfo;
import com.example.abhishek.bookshareapp.ui.adapter.Local.UsersAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserSearchActivity extends AppCompatActivity {

    RecyclerView usersRecyclerView;
    UsersAdapter usersAdapter;
    List<UserInfo> userInfoList = new ArrayList<>();
    SharedPreferences preferences;
    EditText queryEditText;

    private final String TAG = UserSearchActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);

        preferences = getSharedPreferences("Token", MODE_PRIVATE);
        String id = preferences.getString("id", null);

        queryEditText = (EditText) findViewById(R.id.edit_query);

        usersAdapter = new UsersAdapter(id,this, userInfoList, null, null, new UsersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(UserInfo userInfo) {
                Log.i(TAG, "onItemClick");
            }
        });

        usersRecyclerView = (RecyclerView) findViewById(R.id.user_list);

        UsersAPI api = NetworkingFactory.getLocalInstance().getUsersAPI();
        Call<List<UserInfo>> call = api.searchUser(queryEditText.getText().toString());
        call.enqueue(new Callback<List<UserInfo>>() {
            @Override
            public void onResponse(Call<List<UserInfo>> call, Response<List<UserInfo>> response) {
                userInfoList.clear();
                userInfoList.addAll(response.body());
            }

            @Override
            public void onFailure(Call<List<UserInfo>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Check your internet connectivity and try again", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
