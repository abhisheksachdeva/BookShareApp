package com.example.abhishek.bookshareapp.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhishek.bookshareapp.R;
import com.example.abhishek.bookshareapp.api.NetworkingFactory;
import com.example.abhishek.bookshareapp.api.UsersAPI;
import com.example.abhishek.bookshareapp.api.models.LocalBooks.Book;
import com.example.abhishek.bookshareapp.api.models.SignUp.UserInfo;
import com.example.abhishek.bookshareapp.ui.adapter.LocalBooksAdapter;
import com.example.abhishek.bookshareapp.ui.adapter.UsersAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by abhishek on 6/1/2016.
 */
public class BookDetailsActivity extends AppCompatActivity{

    public static final String TAG = BookDetailsActivity.class.getSimpleName();
    Book book;
    public TextView titleBook;
    public TextView authorBook;
    public ImageView imageBook;
    public RatingBar ratingBook;
    public TextView ratingCount;
    List<UserInfo> userInfoList;
    UsersAdapter usersAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_details);

        titleBook = (TextView) findViewById(R.id.row_books_title);
        authorBook = (TextView) findViewById(R.id.row_books_author);
        imageBook = (ImageView) findViewById(R.id.row_books_imageView);
        ratingBook = (RatingBar) findViewById(R.id.row_books_rating);
        ratingCount = (TextView) findViewById(R.id.row_books_ratings_count);

        RecyclerView usersList = (RecyclerView) findViewById(R.id.owner_list);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        usersList.setLayoutManager(layoutManager);
        userInfoList = new ArrayList<>();
        usersAdapter = new UsersAdapter(this, userInfoList, new UsersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(UserInfo userInfo) {
                Log.i(TAG, "onItemClick");
            }
        });
        usersList.setAdapter(usersAdapter);
        String id = getIntent().getExtras().getString("id");
        getBookDetails(id);



    }

    public void getBookDetails(String id){
        UsersAPI api = NetworkingFactory.getLocalInstance().getUsersAPI();
        Call<Book> call = api.getBookDetails(id);
        call.enqueue(new Callback<Book>() {
            @Override
            public void onResponse(Call<Book> call, Response<Book> response) {
                if(response.body()!=null) {
                    Log.d("bda Response:", response.toString());
                    book = response.body();
                    titleBook.setText(book.getTitle());
                    authorBook.setText(book.getAuthor());
                    ratingCount.setText(book.getRatingsCount().toString());
                    ratingBook.setRating(book.getRating());
                    Picasso.with(BookDetailsActivity.this).load(book.getGrImgUrl()).into(imageBook);

                    List<UserInfo> userTempInfoList = book.getUserInfoList();
                    userInfoList.clear();
                    userInfoList.addAll(userTempInfoList);
                    usersAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<Book> call, Throwable t) {
                Log.d("BookDetails fail", t.toString());

            }
        });
    }
}
