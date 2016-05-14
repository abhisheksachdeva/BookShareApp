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
import com.example.abhishek.bookshareapp.utils.CommonUtilities;

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
//        getBookDetails(i,CommonUtilities.API_KEY);



//    }

//    public void getBookDetails(Integer search_id,String key) {

        BookDetailsAPI api = NetworkingFactory.getInstance().getBookDetailsApi();
        Call<GoodreadsResponse> call = api.getBooksDetails(i, CommonUtilities.API_KEY);

        call.enqueue(new Callback<GoodreadsResponse>() {
            @Override
            public void onResponse(Call<GoodreadsResponse> call, Response<GoodreadsResponse> response) {
                if(response.body()!=null) {
                    tempbook = response.body().getBook();
                    title.setText(tempbook.getBk().getBookDetails().getTitle());
                    description.setText(tempbook.getDesc());


                }
                else{
                    title.setText("You're Doomed!!");
                    description.setText(response.toString());

                }

            }

            @Override
            public void onFailure(Call<GoodreadsResponse> call, Throwable t) {

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
