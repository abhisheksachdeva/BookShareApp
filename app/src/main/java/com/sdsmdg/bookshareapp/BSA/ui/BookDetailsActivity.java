package com.sdsmdg.bookshareapp.BSA.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.customtabs.CustomTabsIntent;
import android.text.TextUtils;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.api.NetworkingFactory;
import com.sdsmdg.bookshareapp.BSA.api.UsersAPI;
import com.sdsmdg.bookshareapp.BSA.api.models.LocalBooks.Book;
import com.sdsmdg.bookshareapp.BSA.api.models.LocalBooks.BookAddDeleteResponse;
import com.sdsmdg.bookshareapp.BSA.api.models.LocalBooks.BookDetailWithCancel;
import com.sdsmdg.bookshareapp.BSA.api.models.LocalBooks.RemoveBook;
import com.sdsmdg.bookshareapp.BSA.api.models.LocalUsers.UserInfo;
import com.sdsmdg.bookshareapp.BSA.api.models.VerifyToken.Detail;
import com.sdsmdg.bookshareapp.BSA.ui.adapter.Local.UsersAdapter;
import com.sdsmdg.bookshareapp.BSA.utils.CommonUtilities;
import com.sdsmdg.bookshareapp.BSA.utils.Helper;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.blurry.Blurry;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BookDetailsActivity extends AppCompatActivity {

    public static final String TAG = BookDetailsActivity.class.getSimpleName();

    Book book;
    String title, author, gr_id, gr_img_url, description, rating_count;
    Long ratingsCount;
    Float rating;
    public TextView authorBook;
    TextView bookTitle, bookDescription;
    public RatingBar ratingBook;
    public TextView ratingCount;
    List<UserInfo> userInfoList;
    UsersAdapter usersAdapter;
    String bookId, bookTitleText;
    ImageView image;
    public static String Response;
    FrameLayout rootView;
    NestedScrollView scrollView;
    Boolean showMore = false;
    CustomProgressDialog customProgressDialog;
    Button addToMyLibraryButton, removeFromMyLibraryButton;
    String token, userId;
    SharedPreferences prefs;

    //for chrome custom tabs
    CustomTabsIntent customTabsIntent;
    CustomTabsIntent.Builder intentBuilder;

    public static String getResponse() {
        return Response;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_details);
        prefs = getSharedPreferences("Token", MODE_PRIVATE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        customProgressDialog = new CustomProgressDialog(BookDetailsActivity.this);
        customProgressDialog.setCancelable(false);
        customProgressDialog.show();
        token = prefs.getString("token", null);
        userId = prefs.getString("id", "");
        addToMyLibraryButton = (Button) findViewById(R.id.add_to_my_library);
        removeFromMyLibraryButton = (Button) findViewById(R.id.remove_from_my_library);
        authorBook = (TextView) findViewById(R.id.book_author);
        ratingBook = (RatingBar) findViewById(R.id.book_rating);
        ratingCount = (TextView) findViewById(R.id.ratings_count);
        image = (ImageView) findViewById(R.id.book_image);
        bookTitle = (TextView) findViewById(R.id.book_title);
        bookDescription = (TextView) findViewById(R.id.description);
        rootView = (FrameLayout) findViewById(R.id.root_view);
        scrollView = (NestedScrollView) findViewById(R.id.scroll_view);
        bookDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMore = !showMore;

                if (showMore) {
                    bookDescription.setMaxLines(50);
                    bookDescription.setEllipsize(null);
                } else {
                    bookDescription.setMaxLines(4);
                    bookDescription.setEllipsize(TextUtils.TruncateAt.END);
                }
            }
        });

        SharedPreferences prefs = getSharedPreferences("Token", MODE_PRIVATE);

        String id = getIntent().getExtras().getString("id");
        String idd = prefs.getString("id", "");


        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);


        OkHttpClient.Builder httpclient = new OkHttpClient.Builder().addInterceptor(interceptor);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CommonUtilities.local_books_api_url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpclient.build())
                .build();

        UsersAPI api = retrofit.create(UsersAPI.class);

        Call<BookDetailWithCancel> call = api.getBookDetails(id,id, "Token " + prefs
                .getString("token", null));
        call.enqueue(new Callback<BookDetailWithCancel>() {
            @Override
            public void onResponse(@NonNull Call<BookDetailWithCancel> call, @NonNull Response<BookDetailWithCancel> response) {
                if (response.body() != null && response.body().getBook().getDetail() == null) {
                    book = response.body().getBook();
                    Response = response.toString();
                    Helper.setBookId(book.getId());
                    Helper.setBookTitle(book.getTitle());
                    bookId = book.getId();
                    gr_id = book.getId();
                    bookTitleText = book.getTitle();
                    bookTitle.setText(book.getTitle());
                    title = book.getTitle();
                    //This stores whether the request sent by the user is cancelled or not
                    List<Boolean> cancels =  response.body().getCancels();
                    if (book.getDescription().trim() == "") {
                        bookDescription.setText("No Description Available");
                    } else {
                        bookDescription.setText(book.getDescription());
                    }
                    description = book.getDescription();

                    authorBook.setText("by  " + book.getAuthor());
                    author = book.getAuthor();
                    DecimalFormat formatter = new DecimalFormat("##,##,###");
                    rating_count = formatter.format(book.getRatingsCount());

                    ratingCount.setText("(" + rating_count + ")");
                    ratingsCount = book.getRatingsCount();
                    ratingBook.setRating(book.getRating());
                    rating = book.getRating();
                    Picasso.with(BookDetailsActivity.this).load(book.getGrImgUrl()).into(image);
                    Blurry.with(BookDetailsActivity.this)
                            .radius(25)
                            .sampling(1)
                            .color(Color.argb(66, 0, 0, 0))
                            .async()
                            .capture(findViewById(R.id.book_image))
                            .into((ImageView) findViewById(R.id.book_image));
                    gr_img_url = book.getGrImgUrl();
                    List<UserInfo> userTempInfoList = response.body().getUserInfoList();
                    checkIfOwner(userTempInfoList);
                    userInfoList.clear();
                    userInfoList.addAll(userTempInfoList);
                    usersAdapter.setCancels(cancels);
                    usersAdapter.setBookId(book.getId());
                    usersAdapter.setBookTitle(book.getTitle());
                    usersAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplicationContext(), "Book not found", Toast.LENGTH_SHORT).show();
                }
                TransitionManager.beginDelayedTransition(rootView);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        customProgressDialog.dismiss();
                    }
                }, 1000);

            }

            @Override
            public void onFailure(@NonNull Call<BookDetailWithCancel> call, @NonNull Throwable t) {
                TransitionManager.beginDelayedTransition(rootView);
                customProgressDialog.dismiss();
                Toast.makeText(BookDetailsActivity.this, R.string.connection_failed, Toast.LENGTH_SHORT).show();
                finish();


            }
        });
        RecyclerView usersList = (RecyclerView) findViewById(R.id.reader_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        usersList.setLayoutManager(layoutManager);
        userInfoList = new ArrayList<>();
        usersAdapter = new UsersAdapter(true, idd, this, userInfoList, bookTitleText, bookId, new UsersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(UserInfo userInfo) {
            }
        });
        usersList.setAdapter(usersAdapter);



        //regarding chrome custom tab
        // Initialize intentBuilder
        intentBuilder = new CustomTabsIntent.Builder();

        // Set toolbar(tab) color of your chrome browser
        intentBuilder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));

        // Define entry and exit animation
        intentBuilder.setExitAnimations(this, R.anim.right_to_left_end, R.anim.left_to_right_end);
        intentBuilder.setStartAnimations(this, R.anim.left_to_right_start, R.anim.right_to_left_start);
        intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));

        // build it by setting up all
        customTabsIntent = intentBuilder.build();

    }

    public void checkIfOwner(List<UserInfo> userInfoList) {
        for (UserInfo item : userInfoList) {
            if (item.getId().equals(Helper.getUserId())) {
                addToMyLibraryButton.setVisibility(View.GONE);
                removeFromMyLibraryButton.setVisibility(View.VISIBLE);

            }
        }
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

    public void addToMyLibraryClicked(final View view) {
        ((Button)view).setText("Adding...");
        view.setEnabled(false);
        UsersAPI usersAPI = NetworkingFactory.getLocalInstance().getUsersAPI();
        Call<BookAddDeleteResponse> addBookCall = usersAPI.addBook(Helper.getUserEmail(), title, author, gr_id, ratingsCount, rating, gr_img_url, description, "Token " + token);
        addBookCall.enqueue(new Callback<BookAddDeleteResponse>() {
            @Override
            public void onResponse(@NonNull Call<BookAddDeleteResponse> call, @NonNull Response<BookAddDeleteResponse> response) {
                //getDetail() returns whether the book has been added or not
                ((Button)view).setText("Add to My Library");
                view.setEnabled(true);
                if (response.body() != null) {
                    String detail = response.body().getDetail();
                    Toast.makeText(BookDetailsActivity.this, detail, Toast.LENGTH_SHORT).show();
                    if (detail.equals("Book Added")) {
                        view.animate()
                                .translationY(-view.getHeight())
                                .alpha(0.0f)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        view.setVisibility(View.GONE);
                                        view.setAlpha(1.0f);
                                        view.setTranslationY(0);
                                        removeFromMyLibraryButton.setVisibility(View.VISIBLE);
                                    }
                                });
                        userInfoList.clear();
                        usersAdapter.setCancels(response.body().getCancelList());
                        userInfoList.addAll(response.body().getUserList());
                        usersAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(BookDetailsActivity.this, R.string.connection_failed, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<BookAddDeleteResponse> call, @NonNull Throwable t) {
                ((Button)view).setText("Add to My Library");
                view.setEnabled(true);
                Toast.makeText(BookDetailsActivity.this, R.string.connection_failed, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void goodreads(View view) {

        // go to website goodreads
        customTabsIntent.launchUrl(this, Uri.parse("http://www.goodreads.com/work/best_book/"+bookId));
    }

    public void removeFromMyLibraryClicked(View view) {
        removeBook(view, bookId);
    }

    private void removeBook(final View view, final String bookId) {

        ((Button)view).setText("Removing...");
        view.setEnabled(false);
        RemoveBook removeBook = new RemoveBook();
        removeBook.setBookId(bookId);
        removeBook.setUserId(userId);

        UsersAPI usersAPI = NetworkingFactory.getLocalInstance().getUsersAPI();
        Call<BookAddDeleteResponse> call = usersAPI.removeBook(removeBook, "Token " + prefs.getString("token", null));
        call.enqueue(new Callback<BookAddDeleteResponse>() {
            @Override
            public void onResponse(@NonNull Call<BookAddDeleteResponse> call, @NonNull Response<BookAddDeleteResponse> response) {
                ((Button)view).setText("Remove from My Library");
                view.setEnabled(true);
                if (response.body() != null) {
                    String detail = response.body().getDetail();
                    Toast.makeText(BookDetailsActivity.this, detail, Toast.LENGTH_SHORT).show();
                    if (detail.equals("Successfully Removed!!")){
                        if (response.body().getUserList().size() == 0){
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            }, 500);
                        }else {
                            view.animate()
                                    .translationY(-view.getHeight())
                                    .alpha(0.0f)
                                    .setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            super.onAnimationEnd(animation);
                                            view.setVisibility(View.GONE);
                                            view.setAlpha(1.0f);
                                            view.setTranslationY(0);
                                            addToMyLibraryButton.setVisibility(View.VISIBLE);
                                        }
                                    });
                            userInfoList.clear();
                            usersAdapter.setCancels(response.body().getCancelList());
                            userInfoList.addAll(response.body().getUserList());
                            usersAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BookAddDeleteResponse> call, @NonNull Throwable t) {
                ((Button)view).setText("Remove from My Library");
                view.setEnabled(true);
                Toast.makeText(BookDetailsActivity.this, R.string.connection_failed, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

