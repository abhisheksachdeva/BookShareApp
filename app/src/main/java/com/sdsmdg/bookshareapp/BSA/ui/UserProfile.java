package com.sdsmdg.bookshareapp.BSA.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.api.UsersAPI;
import com.sdsmdg.bookshareapp.BSA.api.models.LocalBooks.Book;
import com.sdsmdg.bookshareapp.BSA.api.models.LocalUsers.UserDetailWithCancel;
import com.sdsmdg.bookshareapp.BSA.api.models.LocalUsers.UserInfo;
import com.sdsmdg.bookshareapp.BSA.ui.adapter.Local.BooksAdapterRequest;
import com.sdsmdg.bookshareapp.BSA.utils.CommonUtilities;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.sentry.event.User;
import jp.wasabeef.blurry.Blurry;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.view.View.GONE;


public class UserProfile extends AppCompatActivity {
    TextView name, emailTextView, address, booksCount;
    UserInfo user;
    List<Book> booksList;
    BooksAdapterRequest adapter;
    ImageView callIcon;
    ImageView profile_picture, background_image;
    String contactNo;
    String email;
    NestedScrollView scrollView;
    CustomProgressDialog customProgressDialog;
    SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        customProgressDialog = new CustomProgressDialog(UserProfile.this);
        customProgressDialog.setCancelable(false);
        customProgressDialog.show();
        prefs = getApplicationContext().getSharedPreferences("Token", Context.MODE_PRIVATE);

        name = (TextView) findViewById(R.id.user_name);
        emailTextView = (TextView) findViewById(R.id.user_email);
        address = (TextView) findViewById(R.id.address);
        callIcon = (ImageView) findViewById(R.id.call_icon);
        profile_picture = (ImageView) findViewById(R.id.profile_picture);
        background_image = (ImageView) findViewById(R.id.background_image);
        booksCount = (TextView) findViewById(R.id.books_count);
        scrollView = (NestedScrollView) findViewById(R.id.scroll);


        String id = getIntent().getExtras().getString("id");

        RecyclerView userBooksList = (RecyclerView) findViewById(R.id.user_books_list_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        userBooksList.setLayoutManager(layoutManager);
        booksList = new ArrayList<>();
        adapter = new BooksAdapterRequest(this, booksList, new BooksAdapterRequest.OnItemClickListener() {
            @Override
            public void onItemClick(Book book) {
                Log.i("Click", "onItemClick");
            }
        }, id);

        userBooksList.setAdapter(adapter);
        userBooksList.setNestedScrollingEnabled(false);

        getUserInfoDetails(id);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return (true);
        }

        return (super.onOptionsItemSelected(item));
    }

    public void getUserInfoDetails(final String id) {
        Log.i("INSIDE ONCLICK ", id + "fklksmlsn");

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpclient = new OkHttpClient.Builder().addInterceptor(interceptor);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CommonUtilities.local_books_api_url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpclient.build())
                .build();


        UsersAPI api = retrofit.create(UsersAPI.class);
        Call<UserDetailWithCancel> call = api.getUserDetails(id, id, "Token " + prefs
                .getString("token", null));
        call.enqueue(new Callback<UserDetailWithCancel>() {
            @Override
            public void onResponse(@NonNull Call<UserDetailWithCancel> call,
                                   @NonNull Response<UserDetailWithCancel> response) {
                if (response.body() != null) {
                    user = response.body().getUserInfo();
                    name.setText(user.getName());
                    email = user.getEmail();
                    emailTextView.setText(email);
                    contactNo = user.getContactNo();
                    hideDisplayCallIcon(contactNo);
                    address.setText(getAddress(user.getRoomNo(), user.getHostel()));
                    getProfilePicture(user.getId());

                    List<Book> booksTempInfoList = user.getUserBookList();
                    String bookCount = "Books(" + booksTempInfoList.size() + ")";
                    booksCount.setText(bookCount);
                    booksList.clear();
                    booksList.addAll(booksTempInfoList);
                    adapter.setCancels(response.body().getCancels());
                    adapter.notifyDataSetChanged();
                }

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        customProgressDialog.dismiss();
                    }
                }, 1000);
            }

            @Override
            public void onFailure(@NonNull Call<UserDetailWithCancel> call, @NonNull Throwable t) {
                Log.d("BookDetails fail", t.toString());
                customProgressDialog.dismiss();

            }
        });
    }

    private void getProfilePicture(final String id) {
        Picasso.Builder builder = new Picasso.Builder(UserProfile.this);
        builder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                exception.printStackTrace();
            }
        });
        builder.downloader(new OkHttp3Downloader(getOkHttpClient()))
                .build()
                .load(CommonUtilities.getAnotherUserImageUrl(id))
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        profile_picture.setImageBitmap(bitmap);
                        background_image.setImageBitmap(bitmap);
                        Blurry.with(getApplicationContext())
                                .radius(40)
                                .sampling(1)
                                .color(Color.argb(66, 0, 0, 0))
                                .async()
                                .capture(findViewById(R.id.background_image))
                                .into((ImageView) findViewById(R.id.background_image));
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        profile_picture.setImageResource(R.drawable.user_default_image);
                        background_image.setImageResource(R.drawable.user_default_image);
                        Blurry.with(getApplicationContext())
                                .radius(40)
                                .sampling(1)
                                .color(Color.argb(66, 0, 0, 0))
                                .async()
                                .capture(findViewById(R.id.background_image))
                                .into((ImageView) findViewById(R.id.background_image));
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                    }
                });
    }

    private OkHttpClient getOkHttpClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request newRequest = chain.request().newBuilder()
                                .addHeader("Authorization", "Token " + prefs
                                        .getString("token", null))
                                .build();
                        return chain.proceed(newRequest);
                    }
                }).build();
        return client;
    }

    private String getAddress(String roomNo, String hostel) {
        String ad;
        if (roomNo == null) {
            ad = hostel;
        } else {
            ad = roomNo + ", " + hostel;
        }
        return ad;
    }

    private void hideDisplayCallIcon(String contactNo) {
        if (contactNo == null || Objects.equals(contactNo, "")) {
            callIcon.setVisibility(GONE);
        }
    }

    public void callClicked(View view) {
        final CharSequence[] items = {"Call", "Copy Contact Number", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
        builder.setTitle("Do you want to :");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (items[which].equals("Call")) {

                    String uri = "tel:" + contactNo.trim();

                    Intent i = new Intent(Intent.ACTION_DIAL);
                    i.setData(Uri.parse(uri));
                    startActivity(i);
                } else if (items[which].equals("Copy Contact Number")) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Contact No.", contactNo.trim());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(UserProfile.this, "Contact Number Copied", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.dismiss();

                }
            }
        });
        builder.show();

    }

    public void emailClicked(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_EMAIL, email);
        startActivity(Intent.createChooser(intent, "Send Email"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}