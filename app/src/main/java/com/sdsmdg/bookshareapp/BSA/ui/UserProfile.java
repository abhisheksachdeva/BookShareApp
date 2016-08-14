package com.sdsmdg.bookshareapp.BSA.ui;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.api.NetworkingFactory;
import com.sdsmdg.bookshareapp.BSA.api.UsersAPI;
import com.sdsmdg.bookshareapp.BSA.api.models.LocalBooks.Book;
import com.sdsmdg.bookshareapp.BSA.api.models.UserInfo;
import com.sdsmdg.bookshareapp.BSA.ui.adapter.Local.BooksAdapterSimple;
import com.sdsmdg.bookshareapp.BSA.utils.CommonUtilities;
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
    BooksAdapterSimple adapter;
    ImageView profile_picture, background_image;
    String contactNo;
    String email;
    LinearLayout l1, l2 ;
    NestedScrollView scrollView;
    ProgressBar progress;
    Button dismiss;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name = (TextView)findViewById(R.id.user_name);
        emailTextView = (TextView)findViewById(R.id.user_email);
        address = (TextView)findViewById(R.id.address);
        profile_picture = (ImageView) findViewById(R.id.profile_picture);
        background_image = (ImageView) findViewById(R.id.background_image);
        booksCount = (TextView) findViewById(R.id.books_count);
        scrollView = (NestedScrollView) findViewById(R.id.scroll);
        scrollView.getForeground().setAlpha(180);
        l1 = (LinearLayout)findViewById(R.id.layoutp1);
        l2 = (LinearLayout)findViewById(R.id.layoutp2);
        progress = (ProgressBar) findViewById(R.id.progress);
        dismiss = (Button)findViewById(R.id.dismiss);
        dismiss.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                scrollView.getForeground().setAlpha(0);
                progress.setVisibility(View.GONE);
                l1.setVisibility(View.GONE);
                l2.setVisibility(View.GONE);
            }
        });


        String id = getIntent().getExtras().getString("id");

        RecyclerView userBooksList = (RecyclerView) findViewById(R.id.user_books_list_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        userBooksList.setLayoutManager(layoutManager);
        booksList = new ArrayList<>();
        adapter = new BooksAdapterSimple(this, booksList, new BooksAdapterSimple.OnItemClickListener() {
            @Override
            public void onItemClick(Book book) {
                Log.i("Click", "onItemClick");
            }
        });

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
                    booksCount.setText(booksTempInfoList.size()+"");
                    booksList.clear();
                    booksList.addAll(booksTempInfoList);
                    adapter.notifyDataSetChanged();
                }

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progress.setVisibility(View.GONE);
                        l1.setVisibility(View.GONE);
                        l2.setVisibility(View.GONE);
                        scrollView.getForeground().setAlpha(0);

                    }
                }, 1000);
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                Log.d("BookDetails fail", t.toString());
                progress.setVisibility(View.GONE);
                l1.setVisibility(View.GONE);
                l2.setVisibility(View.GONE);
                scrollView.getForeground().setAlpha(0);
            }
        });
    }

    public void callClicked(View view) {

        String uri = "tel:" + contactNo.trim();

        Intent i = new Intent(Intent.ACTION_DIAL);
        i.setData(Uri.parse(uri));
        startActivity(i);
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