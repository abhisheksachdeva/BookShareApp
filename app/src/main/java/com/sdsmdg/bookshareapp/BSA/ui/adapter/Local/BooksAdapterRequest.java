package com.sdsmdg.bookshareapp.BSA.ui.adapter.Local;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.api.NetworkingFactory;
import com.sdsmdg.bookshareapp.BSA.api.UsersAPI;
import com.sdsmdg.bookshareapp.BSA.api.models.LocalBooks.Book;
import com.sdsmdg.bookshareapp.BSA.api.models.Notification.Notifications;
import com.sdsmdg.bookshareapp.BSA.utils.Helper;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BooksAdapterRequest extends RecyclerView.Adapter<BooksAdapterRequest.ViewHolder> {

    private Context context;
    private List<Book> bookList;
    Book tempValues = null;
    SharedPreferences prefs;

    String userId;
    private final OnItemClickListener listener;

    List<Boolean> cancels;

    public interface OnItemClickListener {
        public void onItemClick(Book book);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleBook;
        public TextView authorBook;
        public ImageView imageBook;
        public RatingBar ratingBook;
        public TextView ratingCount;
        public Button request;
        Context context;

        public ViewHolder(View v, Context context) {
            super(v);
            titleBook = (TextView) v.findViewById(R.id.row_books_title);
            authorBook = (TextView) v.findViewById(R.id.row_books_author);
            imageBook = (ImageView) v.findViewById(R.id.row_books_imageView);
            ratingBook = (RatingBar) v.findViewById(R.id.row_books_rating);
            ratingCount = (TextView) v.findViewById(R.id.row_books_ratings_count);
            request = (Button) v.findViewById(R.id.requestButton);
            this.context = context;
        }

    }

    public BooksAdapterRequest(Context context, List<Book> bookList, OnItemClickListener listener, String userId) {
        this.bookList = bookList;
        this.context = context;
        this.listener = listener;
        this.userId = userId;
        prefs = context.getSharedPreferences("Token", Context.MODE_PRIVATE);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_books_request, parent, false);
        ViewHolder vh = new ViewHolder(v, context);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final String bookTitle, bookId;
        tempValues = bookList.get(position);
        bookId = tempValues.getId();
        bookTitle = tempValues.getTitle();
        holder.titleBook.setText(tempValues.getTitle());
        holder.authorBook.setText(tempValues.getAuthor());

        if (cancels.get(position)) {
            holder.request.setText("Cancel");
        } else {
            holder.request.setText("Request");
        }

        if (!tempValues.getGrImgUrl().isEmpty()) {
            Picasso.with(this.context).load(tempValues.getGrImgUrl()).placeholder(R.drawable.default_book_image).into(holder.imageBook);
        }

        holder.ratingBook.setRating(tempValues.getRating());
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        String rating_count = formatter.format(tempValues.getRatingsCount());
        holder.ratingCount.setText("(" + rating_count + ")");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(bookList.get(position));
            }
        });

        holder.request.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                if (!cancels.get(position)) {
                    builder.setTitle("Do you want to send a request?");
                } else {
                    builder.setTitle("Do you want to cancel the request?");
                }
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(holder.request.getText().equals("Request")) {
                            String process = "request";
                            UsersAPI usersAPI = NetworkingFactory.getLocalInstance().getUsersAPI();
                            Call<Notifications> sendNotif = usersAPI.sendNotif(Helper.getUserId(), Helper.getUserName(), bookId, bookTitle, process, userId, "request for", "Token " + prefs
                                    .getString("token", null));
                            sendNotif.enqueue(new Callback<Notifications>() {
                                @Override
                                public void onResponse(Call<Notifications> call, Response<Notifications> response) {
                                    if (response.body() != null) {
                                        Toast.makeText(context, response.body().getDetail(), Toast.LENGTH_SHORT).show();
                                        holder.request.setText("Cancel");
                                    } else {
                                        Toast.makeText(context, response.body().getDetail(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Notifications> call, Throwable t) {
                                    Toast.makeText(context, R.string.connection_failed, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {

                            String process = "cancel";
                            UsersAPI usersAPI = NetworkingFactory.getLocalInstance().getUsersAPI();
                            Call<Notifications> cancelNotif = usersAPI.cancelNotif(bookId, userId, process, "Token " + prefs
                                    .getString("token", null));
                            cancelNotif.enqueue(new Callback<Notifications>() {
                                @Override
                                public void onResponse(Call<Notifications> call, Response<Notifications> response) {
                                    if (response.body() != null) {
                                        Toast.makeText(context, response.body().getDetail(), Toast.LENGTH_SHORT).show();
                                        holder.request.setText("Request");

                                    } else {
                                        Toast.makeText(context, response.body().getDetail(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Notifications> call, Throwable t) {
                                    Toast.makeText(context, R.string.connection_failed, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
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

    public void setCancels(List<Boolean> cancels) {
        this.cancels = cancels;
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
