package com.example.abhishek.bookshareapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.abhishek.bookshareapp.R;
import com.example.abhishek.bookshareapp.api.UsersAPI;
import com.example.abhishek.bookshareapp.api.models.Notification.Notifications;
import com.example.abhishek.bookshareapp.ui.adapter.Local.NotificationAdapter;
import com.example.abhishek.bookshareapp.utils.CommonUtilities;
import com.example.abhishek.bookshareapp.utils.Helper;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NotificationActivity extends AppCompatActivity{

    RecyclerView notificationsListView;
    LinearLayoutManager nLinearLayoutManager;
    NotificationAdapter adapter,d;
    SwipeRefreshLayout refreshLayout;
    List<Notifications> notificationsList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        nLinearLayoutManager = new LinearLayoutManager(this);
        nLinearLayoutManager.setReverseLayout(true);
        nLinearLayoutManager.setStackFromEnd(true);

        notificationsListView = (RecyclerView) findViewById(R.id.notifications_list);
        notificationsListView.setLayoutManager(nLinearLayoutManager);

        adapter = new NotificationAdapter(this, notificationsList);
        notificationsListView.setAdapter(adapter);

        getNotifications();

        refreshLayout =(SwipeRefreshLayout)findViewById(R.id.notif_refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNotifications();
            }
        });

    }

    public void getNotifications() {
        Helper.setOld_total(Helper.getNew_total());

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpclient = new OkHttpClient.Builder();

        httpclient.addInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CommonUtilities.local_books_api_url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpclient.build())
                .build();

        UsersAPI usersAPI = retrofit.create(UsersAPI.class);
        Log.i("NotifActivity", Helper.getUserId());
        Call<List<Notifications>> call = usersAPI.getNotifs(Helper.getUserId());
        call.enqueue(new Callback<List<Notifications>>() {
            @Override
            public void onResponse(Call<List<Notifications>> call, Response<List<Notifications>> response) {
                if (response.body() != null) {
                    List<Notifications> notifList = response.body();
                    notificationsList.clear();
                    Helper.setNew_total(notifList.size());
                    notificationsList.addAll(notifList);
                    adapter.notifyDataSetChanged();
                    refreshLayout.setRefreshing(false);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
        finish();
    }
}