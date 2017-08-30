package com.sdsmdg.bookshareapp.BSA.ui.adapter.GR;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.api.BooksAPI;
import com.sdsmdg.bookshareapp.BSA.api.NetworkingFactory;
import com.sdsmdg.bookshareapp.BSA.api.UsersAPI;
import com.sdsmdg.bookshareapp.BSA.api.models.Book;
import com.sdsmdg.bookshareapp.BSA.api.models.BookDescription;
import com.sdsmdg.bookshareapp.BSA.api.models.GoodreadsResponse2;
import com.sdsmdg.bookshareapp.BSA.utils.CommonUtilities;
import com.sdsmdg.bookshareapp.BSA.utils.Helper;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BooksAdapterGR extends RecyclerView.Adapter<BooksAdapterGR.ViewHolder> {

    private Context context;
    private List<Book> bookList;
    Book tempValues = null;
    BookDescription tempDescp;
    String description;
    String token;
    SharedPreferences prefs;

    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        public void onItemClick(com.sdsmdg.bookshareapp.BSA.api.models.Book book);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleBook;
        public TextView authorBook;
        public ImageView imageBook;
        public RatingBar ratingBook;
        public TextView ratingCount;
        public Button add;

        Context context;

        public ViewHolder(View v, Context context) {
            super(v);
            titleBook = (TextView) v.findViewById(R.id.row_books_title);
            authorBook = (TextView) v.findViewById(R.id.row_books_author);
            imageBook = (ImageView) v.findViewById(R.id.row_books_imageView);
            ratingBook = (RatingBar) v.findViewById(R.id.row_books_rating);
            ratingCount = (TextView) v.findViewById(R.id.row_books_ratings_count);
            add = (Button) v.findViewById(R.id.add);

            this.context = context;
        }

    }

    public BooksAdapterGR(Context context, List<com.sdsmdg.bookshareapp.BSA.api.models.Book> bookList, OnItemClickListener listener) {
        this.bookList = bookList;
        this.context = context;
        this.listener = listener;
        prefs = context.getSharedPreferences("Token", Context.MODE_PRIVATE);


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_books_add, parent, false);

        ViewHolder vh = new ViewHolder(v, context);

        return vh;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final String email, title, author, gr_id, gr_img_url;
        final Integer search_id;
        final Long ratingsCount;
        final Float rating;
        token = prefs.getString("token", null);

        holder.add.setEnabled(true);
        tempValues = bookList.get(position);
        holder.titleBook.setText(tempValues.getBookDetails().getTitle());
        holder.authorBook.setText(tempValues.getBookDetails().getAuthor().getAuthor_name());
        Picasso.with(this.context).load(tempValues.getBookDetails().getImage_url()).into(holder.imageBook);
        holder.ratingBook.setRating(tempValues.getRating());

        DecimalFormat formatter = new DecimalFormat("#,###,###");

        String rating_count = formatter.format(tempValues.getRatingCount());

        holder.ratingCount.setText("(" + rating_count + ")");
        title = tempValues.getBookDetails().getTitle();
        email = Helper.getUserEmail();
        author = tempValues.getBookDetails().getAuthor().getAuthor_name();
        gr_img_url = tempValues.getBookDetails().getImage_url();
        rating = tempValues.getRating();
        ratingsCount = tempValues.getRatingCount();
        gr_id = tempValues.getId().toString();
        search_id = tempValues.getBookDetails().getId();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(bookList.get(position));
            }
        });

        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BooksAPI api = NetworkingFactory.getGRInstance().getBooksApi();
                Call<GoodreadsResponse2> call = api.getBookDescription(search_id, CommonUtilities.API_KEY);
                call.enqueue(new Callback<GoodreadsResponse2>() {
                    @Override
                    public void onResponse(Call<GoodreadsResponse2> call, Response<GoodreadsResponse2> response) {
                        if (response != null) {
                            tempDescp = response.body().getbDesc();
                            description = tempDescp.getBDescription();

                            description = Html.fromHtml(description).toString();
                            if (description.length() > 1000) {
                                description = description.substring(0, 990) + "...";
                            }
                        } else {
                            description = "No Description Available";
                        }
                    }

                    @Override
                    public void onFailure(Call<GoodreadsResponse2> call, Throwable t) {
                        description = "No Description Available";
                    }
                });

                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AppTheme_Dialog));
                builder.setInverseBackgroundForced(true);
                builder.setTitle("Do you want to add this Book?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UsersAPI usersAPI = NetworkingFactory.getLocalInstance().getUsersAPI();
                        Call<com.sdsmdg.bookshareapp.BSA.api.models.LocalBooks.Book> addBook = usersAPI.addBook(email, title, author, gr_id, ratingsCount, rating, gr_img_url, description, "Token " + token);
                        addBook.enqueue(new Callback<com.sdsmdg.bookshareapp.BSA.api.models.LocalBooks.Book>() {

                            @Override
                            public void onResponse(Call<com.sdsmdg.bookshareapp.BSA.api.models.LocalBooks.Book> call, Response<com.sdsmdg.bookshareapp.BSA.api.models.LocalBooks.Book> response) {
                                try {

                                    if (response.body() != null) {
                                        Toast.makeText(context, response.body().getDetail(), Toast.LENGTH_SHORT).show();
                                        holder.add.setEnabled(false);
                                        holder.add.setTextColor(Color.argb(255, 179, 179, 179));
                                    } else {
                                        Toast.makeText(context, response.body().getDetail(), Toast.LENGTH_SHORT).show();
                                    }
                                } catch (NullPointerException n) {
                                    Toast.makeText(context, "Sorry, this book can't be added", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<com.sdsmdg.bookshareapp.BSA.api.models.LocalBooks.Book> call, Throwable t) {
                                Toast.makeText(context, R.string.connection_failed, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();

            }
        });

    }

    @Override
    public int getItemCount() {
        if (bookList != null)
            return bookList.size();

        return 0;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}