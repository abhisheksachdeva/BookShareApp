package com.example.abhishek.bookshareapp.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookDetailsActivity extends AppCompatActivity{

    public static final String TAG = BookDetailsActivity.class.getSimpleName();
    Book book;
    String title,author,gr_id,gr_img_url;
    Long ratingsCount;
    Float rating;
    public TextView titleBook;
    public TextView authorBook;
    public ImageView imageBook;
    public RatingBar ratingBook;
    public TextView ratingCount;
    public Button addBooks;
    List<UserInfo> userInfoList;
    UsersAdapter usersAdapter;
    String bookId,bookTitle;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_details);

        titleBook = (TextView) findViewById(R.id.row_books_title);
        authorBook = (TextView) findViewById(R.id.row_books_author);
        imageBook = (ImageView) findViewById(R.id.row_books_imageView);
        ratingBook = (RatingBar) findViewById(R.id.row_books_rating);
        ratingCount = (TextView) findViewById(R.id.row_books_ratings_count);
        addBooks = (Button) findViewById(R.id.addBook);
        String id = getIntent().getExtras().getString("id");


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
                    titleBook.setText(book.getTitle()); title = book.getTitle();
                    authorBook.setText(book.getAuthor()); author = book.getAuthor();
                    ratingCount.setText(book.getRatingsCount().toString()+" votes"); ratingsCount=book.getRatingsCount();
                    ratingBook.setRating(book.getRating());rating = book.getRating();
                    Picasso.with(BookDetailsActivity.this).load(book.getGrImgUrl()).into(imageBook); gr_img_url = book.getGrImgUrl();
                    List<UserInfo> userTempInfoList = book.getUserInfoList();
                    userInfoList.clear();
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
        usersAdapter = new UsersAdapter(id,this, userInfoList,bookTitle,bookId, new UsersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(UserInfo userInfo) {
                Log.i(TAG, "onItemClick");
            }
        });
        usersList.setAdapter(usersAdapter);


        addBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = { "Yes", "No"};
                AlertDialog.Builder builder = new AlertDialog.Builder(BookDetailsActivity.this);
                builder.setTitle("Do you want to add this Book?");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (items[which].equals("Yes")){
                            UsersAPI usersAPI = NetworkingFactory.getLocalInstance().getUsersAPI();
                            final Call<com.example.abhishek.bookshareapp.api.models.LocalBooks.Book> addBook = usersAPI.addBook(Helper.getUserEmail(),title, author,gr_id,ratingsCount,rating,gr_img_url);
                            addBook.enqueue(new Callback<com.example.abhishek.bookshareapp.api.models.LocalBooks.Book>() {
                                @Override
                                public void onResponse(Call<com.example.abhishek.bookshareapp.api.models.LocalBooks.Book> call, Response<com.example.abhishek.bookshareapp.api.models.LocalBooks.Book> response) {
                                    Log.i("Email iD ", Helper.getUserEmail());
                                    if (response.body() != null) {
                                        Log.i("AddBook", "Success");
                                        Toast.makeText(BookDetailsActivity.this, response.body().getDetail(), Toast.LENGTH_SHORT).show();
                                        Log.i("response", response.body().getDetail());
                                        addBooks.setEnabled(false);

                                    } else {
                                        Log.i("AddBook", "Response Null");
                                        Toast.makeText(BookDetailsActivity.this, response.body().getDetail() , Toast.LENGTH_SHORT).show();
                                    }
                                }
                                @Override
                                public void onFailure(Call<com.example.abhishek.bookshareapp.api.models.LocalBooks.Book> call, Throwable t) {
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
    }

}
