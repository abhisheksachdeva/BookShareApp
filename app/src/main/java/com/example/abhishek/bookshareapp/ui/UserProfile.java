package com.example.abhishek.bookshareapp.ui;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhishek.bookshareapp.R;
import com.example.abhishek.bookshareapp.api.NetworkingFactory;
import com.example.abhishek.bookshareapp.api.UsersAPI;
import com.example.abhishek.bookshareapp.api.models.LocalBooks.Book;
import com.example.abhishek.bookshareapp.api.models.UserInfo;
import com.example.abhishek.bookshareapp.ui.adapter.Local.BooksAdapterSimple;
import com.example.abhishek.bookshareapp.utils.CommonUtilities;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.blurry.Blurry;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfile extends AppCompatActivity {
    TextView name,email,address, booksCount;
    UserInfo user;
    List<Book> booksList;
    BooksAdapterSimple adapter;
    ImageView profile_picture, background_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        name = (TextView)findViewById(R.id.name);
        email = (TextView)findViewById(R.id.email);
        address = (TextView)findViewById(R.id.address);
        profile_picture = (ImageView) findViewById(R.id.profile_picture);
        background_image = (ImageView) findViewById(R.id.background_image);
        booksCount = (TextView) findViewById(R.id.books_count);
        String id = getIntent().getExtras().getString("id");

        RecyclerView userBooksList = (RecyclerView) findViewById(R.id.userBooksLists);
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
                    email.setText(user.getEmail());
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
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                Log.d("BookDetails fail", t.toString());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}