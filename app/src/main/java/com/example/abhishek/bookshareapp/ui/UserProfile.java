package com.example.abhishek.bookshareapp.ui;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfile extends AppCompatActivity {
    TextView userName,userEmail,address;
    UserInfo user;
    List<Book> booksList;
    BooksAdapterSimple adapter;
    CollapsingToolbarLayout collapsingToolbarLayout;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        userName = (TextView)findViewById(R.id.username);
        userEmail = (TextView)findViewById(R.id.useremail);
        address = (TextView)findViewById(R.id.address);

        String id = getIntent().getExtras().getString("id");

        image = (ImageView) findViewById(R.id.img);
        image.setImageDrawable(getResources().getDrawable(R.drawable.books));
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar1));
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("User Profile");
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        setPalette();


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
                    userName.setText( user.getFirstName() + " "+user.getLastName());
                    userEmail.setText( user.getEmail());
                    collapsingToolbarLayout.setTitle(user.getFirstName() + " "+user.getLastName());
                    address.setText( user.getRoomNo()+", "+user.getHostel());
                    String url = CommonUtilities.local_books_api_url + "image/"+id+"/";
                    Picasso.with(UserProfile.this).load(url).into(image);
                    List<Book> booksTempInfoList = user.getUserBookList();
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

    private void setPalette() {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                int primaryDark = getResources().getColor(R.color.colorPrimaryDark);
                int primary = getResources().getColor(R.color.colorPrimary);
                collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(primary));
                collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkVibrantColor(primaryDark));
            }
        });
    }

}