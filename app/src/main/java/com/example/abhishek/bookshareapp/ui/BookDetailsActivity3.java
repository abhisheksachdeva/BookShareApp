package com.example.abhishek.bookshareapp.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhishek.bookshareapp.R;
import com.example.abhishek.bookshareapp.api.NetworkingFactory;
import com.example.abhishek.bookshareapp.api.UsersAPI;
import com.example.abhishek.bookshareapp.api.models.LocalBooks.Book;
import com.example.abhishek.bookshareapp.api.models.UserInfo;
import com.example.abhishek.bookshareapp.ui.adapter.Local.UsersAdapter;
import com.example.abhishek.bookshareapp.utils.Helper;
import com.klinker.android.sliding.SlidingActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookDetailsActivity3 extends SlidingActivity{

    public static final String TAG = BookDetailsActivity3.class.getSimpleName();
    Book book;
    String title,author,gr_id,gr_img_url;
    Long ratingsCount;
    Float rating;
    public TextView authorBook;
    public RatingBar ratingBook;
    public TextView ratingCount;
    List<UserInfo> userInfoList;
    UsersAdapter usersAdapter;
    String bookId,bookTitle;
    ImageView image;


    @Override
    public void init(Bundle savedInstanceState) {
        setTitle("Activity Title");

        setPrimaryColors(
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorPrimaryDark)
        );

        setContent(R.layout.activity_books_details3);
        authorBook = (TextView) findViewById(R.id.row_books_author);
        ratingBook = (RatingBar) findViewById(R.id.row_books_rating);
        ratingCount = (TextView) findViewById(R.id.row_books_ratings_count);
        image = (ImageView) findViewById(R.id.row_books_imageView);
        setImage(R.drawable.b_image);
        setTitle("Book Details");
        setFab(R.color.BGyellow, R.drawable.plus, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = { "Yes", "No"};
                AlertDialog.Builder builder = new AlertDialog.Builder(BookDetailsActivity3.this);
                builder.setTitle("Do you want to add this Book?");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (items[which].equals("Yes")){
                            UsersAPI usersAPI = NetworkingFactory.getLocalInstance().getUsersAPI();
                            final Call<Book> addBook = usersAPI.addBook(Helper.getUserEmail(),title, author,gr_id,ratingsCount,rating,gr_img_url);
                            Log.d("sss",Helper.getUserEmail()+" "+title+author+gr_id+gr_img_url+rating+ratingsCount+gr_img_url);
                            addBook.enqueue(new Callback<Book>() {
                                @Override
                                public void onResponse(Call<Book> call, Response<Book> response) {
                                    Log.i("Email iD ", Helper.getUserEmail());
                                    if (response.body() != null) {
                                        Log.i("AddBook", "Success");
                                        Toast.makeText(BookDetailsActivity3.this, response.body().getDetail(), Toast.LENGTH_SHORT).show();
                                        Log.i("response", response.body().getDetail());

                                    } else {
                                        Log.i("AddBook", "Response Null");
                                        Toast.makeText(BookDetailsActivity3.this, response.body().getDetail()+"ssss" , Toast.LENGTH_SHORT).show();

                                    }
                                }
                                @Override
                                public void onFailure(Call<Book> call, Throwable t) {
                                    Log.i("AddBook","Failed!!");
                                }
                            });
                        }
                        else{
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });


        SharedPreferences prefs = getSharedPreferences("Token", MODE_PRIVATE);

        String id = getIntent().getExtras().getString("id");
        String idd = prefs.getString("id", "");

        UsersAPI api = NetworkingFactory.getLocalInstance().getUsersAPI();
        Call<Book> call = api.getBookDetails(id);
        call.enqueue(new Callback<Book>() {
            @Override
            public void onResponse(Call<Book> call, Response<Book> response) {
                if(response.body()!=null) {
                    Log.d("bda Response:", response.toString());
                    book = response.body();
                    Helper.setBookId(book.getId());
                    Helper.setBookTitle(book.getTitle());
                    bookId=book.getId(); gr_id = book.getId();
                    bookTitle=book.getTitle();
                    title = book.getTitle();
                    authorBook.setText("By : "+book.getAuthor()); author = book.getAuthor();
                    ratingCount.setText("Having "+book.getRatingsCount().toString()+" votes"); ratingsCount=book.getRatingsCount();
                    ratingBook.setRating(book.getRating());rating = book.getRating();
                    Picasso.with(BookDetailsActivity3.this).load(book.getGrImgUrl()).into(image);
                    gr_img_url = book.getGrImgUrl();
                    List<UserInfo> userTempInfoList = book.getUserInfoList();
                    userInfoList.clear();
                    setTitle(bookTitle);
                    userInfoList.addAll(userTempInfoList);
                    usersAdapter.setBookId(book.getId());
                    usersAdapter.setBookTitle(book.getTitle());
                    usersAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<Book> call, Throwable t) {
                Log.d("BookDetails fail", t.toString());

            }
        });
        RecyclerView usersList = (RecyclerView) findViewById(R.id.owner_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        usersList.setLayoutManager(layoutManager);
        userInfoList = new ArrayList<>();
        usersAdapter = new UsersAdapter(idd,this, userInfoList,bookTitle,bookId, new UsersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(UserInfo userInfo) {
                Log.i(TAG, "onItemClick");
            }
        });
        usersList.setAdapter(usersAdapter);

    }




    }
