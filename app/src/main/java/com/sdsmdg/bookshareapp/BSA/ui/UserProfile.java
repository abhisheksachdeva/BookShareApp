package com.sdsmdg.bookshareapp.BSA.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.api.NetworkingFactory;
import com.sdsmdg.bookshareapp.BSA.api.UsersAPI;
import com.sdsmdg.bookshareapp.BSA.api.models.LocalBooks.Book;
import com.sdsmdg.bookshareapp.BSA.api.models.Notification.Notifications;
import com.sdsmdg.bookshareapp.BSA.api.models.UserInfo;
import com.sdsmdg.bookshareapp.BSA.ui.adapter.Local.BooksAdapterRequest;
import com.sdsmdg.bookshareapp.BSA.utils.CommonUtilities;
import com.sdsmdg.bookshareapp.BSA.utils.Helper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.blurry.Blurry;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UserProfile extends AppCompatActivity {
    TextView name,emailTextView,address, booksCount;
    UserInfo user;
    List<Book> booksList;
    BooksAdapterRequest adapter;
    ImageView profile_picture, background_image;
    String contactNo;
    String email;
    NestedScrollView scrollView;
    CustomProgressDialog customProgressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        customProgressDialog = new CustomProgressDialog(UserProfile.this);
        customProgressDialog.setCancelable(false);
        customProgressDialog.show();
        customProgressDialog.getWindow().setLayout(464, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);

        name = (TextView)findViewById(R.id.user_name);
        emailTextView = (TextView)findViewById(R.id.user_email);
        address = (TextView)findViewById(R.id.address);
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
        },id);

        userBooksList.setAdapter(adapter);
        userBooksList.setNestedScrollingEnabled(false);

        getUserInfoDetails(id);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return(true);
        }

        return(super.onOptionsItemSelected(item));
    }

    public void getUserInfoDetails(final String id){
        UsersAPI api = NetworkingFactory.getLocalInstance().getUsersAPI();
        Call<UserInfo> call = api.getUserDetails(id);
        call.enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                if(response.body()!=null) {
                    Log.d("UserProfile Response:", response.toString());
                    user = response.body();
                    name.setText(user.getName());

                    email = user.getEmail();
                    emailTextView.setText(email);
                    contactNo = user.getContactNo();
                    String ad = user.getRoomNo()+", "+user.getHostel();
                    address.setText(ad);
                    String url = CommonUtilities.local_books_api_url + "image/"+id+"/";
                    Picasso.with(UserProfile.this).load(url).into(profile_picture);
                    Picasso.with(UserProfile.this).load(url).into(background_image);
                    Blurry.with(UserProfile.this)
                            .radius(40)
                            .sampling(1)
                            .color(Color.argb(66, 0, 0, 0))
                            .async()
                            .capture(findViewById(R.id.background_image))
                            .into((ImageView) findViewById(R.id.background_image));
                    List<Book> booksTempInfoList = user.getUserBookList();
                    String bookCount= "Books("+booksTempInfoList.size()+")";
                    booksCount.setText(bookCount);
                    booksList.clear();
                    booksList.addAll(booksTempInfoList);
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
            public void onFailure(Call<UserInfo> call, Throwable t) {
                Log.d("BookDetails fail", t.toString());
                customProgressDialog.dismiss();

            }
        });
    }

    public void callClicked(View view) {
        final CharSequence[] items = { "Call", "Copy Contact Number","Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
        builder.setTitle("Do you want to :");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (items[which].equals("Call")){

                    String uri = "tel:" + contactNo.trim();

                    Intent i = new Intent(Intent.ACTION_DIAL);
                    i.setData(Uri.parse(uri));
                    startActivity(i);
                }
                else if(items[which].equals("Copy Contact Number")) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Contact No.",contactNo.trim());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(UserProfile.this,"Contact Number Copied",Toast.LENGTH_SHORT).show();
                }
                else{
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