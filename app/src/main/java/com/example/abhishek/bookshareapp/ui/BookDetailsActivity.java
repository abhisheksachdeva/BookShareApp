package com.example.abhishek.bookshareapp.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.abhishek.bookshareapp.R;
import com.example.abhishek.bookshareapp.api.BookDetailsAPI;
import com.example.abhishek.bookshareapp.api.NetworkingFactory;
import com.example.abhishek.bookshareapp.api.models.Book2;
import com.example.abhishek.bookshareapp.api.models.GoodreadsResponse;
import com.example.abhishek.bookshareapp.api.models.GoodreadsResponse2;
import com.example.abhishek.bookshareapp.utils.CommonUtilities;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookDetailsActivity extends AppCompatActivity {

    TextView title,description;
    ImageView img;
    RatingBar stars;
    String id;
    Integer i;
    Book2 tempbook;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);
        id= getIntent().getExtras().getString("search_id");
        title = (TextView) findViewById(R.id.book_detail_title);
        description = (TextView) findViewById(R.id.book_detail_description);
        img = (ImageView) findViewById(R.id.book_detail_image);
        stars = (RatingBar) findViewById(R.id.book_detail_rating);
        i=Integer.parseInt(id);
        getBookDetails(i);




    }

    public void getBookDetails(Integer search_id) {

        BookDetailsAPI api = NetworkingFactory.getInstance().getBookDetailsApi();
        Call<GoodreadsResponse2> call = api.getBooksDetails(search_id, CommonUtilities.API_KEY);
        Log.d("resp", "fhgfghghghgf");


        call.enqueue(new Callback<GoodreadsResponse2>() {
            @Override
            public void onResponse(Call<GoodreadsResponse2> call, Response<GoodreadsResponse2> response) {
                if(response.body()!=null) {
                    Log.d("bookresp", response.toString());
                    tempbook = response.body().getBook();
                    title.setText(tempbook.getTitle());
                    description.setText(tempbook.getDesc());
                    Picasso.with(getBaseContext()).load(tempbook.getImage_url()).into(img);
                    stars.setRating(tempbook.getRating());




                }
                else{
                    title.setText("You're Doomed!!");
                    description.setText(response.toString());

                }

            }

            @Override
            public void onFailure(Call<GoodreadsResponse2> call, Throwable t) {

                Log.d("searchresp", "searchOnFail " + t.toString());

            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
       finish();
    }
}
