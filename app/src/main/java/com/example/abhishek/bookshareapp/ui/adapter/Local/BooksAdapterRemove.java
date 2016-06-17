package com.example.abhishek.bookshareapp.ui.adapter.Local;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhishek.bookshareapp.R;
import com.example.abhishek.bookshareapp.api.NetworkingFactory;
import com.example.abhishek.bookshareapp.api.UsersAPI;
import com.example.abhishek.bookshareapp.api.models.LocalBooks.Book;
import com.example.abhishek.bookshareapp.api.models.LocalBooks.RemoveBook;
import com.example.abhishek.bookshareapp.api.models.VerifyToken.Detail;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BooksAdapterRemove extends RecyclerView.Adapter<BooksAdapterRemove.ViewHolder> {

    private Context context;
    private List<Book> bookList;
    Book tempValues = null;
    private final OnItemClickListener listener;
    SharedPreferences preferences;
    String userId;

    public interface OnItemClickListener {
        public void onItemClick(Book book);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleBook;
        public TextView authorBook;
        public ImageView imageBook;
        public RatingBar ratingBook;
        public TextView ratingCount;
        public Button removeButton;
        Context context;

        public ViewHolder(View v, Context context) {
            super(v);
            titleBook = (TextView) v.findViewById(R.id.book_title);
            authorBook = (TextView) v.findViewById(R.id.author);
            imageBook = (ImageView) v.findViewById(R.id.book_cover);
            ratingBook = (RatingBar) v.findViewById(R.id.book_rating);
            ratingCount = (TextView) v.findViewById(R.id.ratings_count);
            removeButton = (Button) v.findViewById(R.id.remove_button);
            this.context = context;
        }

    }

    public BooksAdapterRemove(String userId, Context context, List<Book> bookList, OnItemClickListener listener) {
        this.bookList = bookList;
        this.context = context;
        this.listener = listener;
        this.userId = userId;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_books_remove, parent, false);
        return (new ViewHolder(v, context));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        tempValues = bookList.get(position);

        holder.titleBook.setText(tempValues.getTitle());
        holder.authorBook.setText(tempValues.getAuthor());
        if(!tempValues.getGrImgUrl().isEmpty()) {
            Picasso.with(this.context).load(tempValues.getGrImgUrl()).into(holder.imageBook);
        }
        holder.ratingBook.setRating(tempValues.getRating());
        holder.ratingCount.setText(tempValues.getRatingsCount() + " votes");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(bookList.get(position));
            }
        });

        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = { "Yes", "No"};
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Do you want to remove this Book?");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (items[which].equals("Yes")) {
                            removeBook(tempValues.getId(), position);
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

    public void removeBook(String bookId, final int position) {

        RemoveBook removeBook = new RemoveBook();
        removeBook.setBookId(bookId);
        removeBook.setUserId(userId);

        UsersAPI usersAPI = NetworkingFactory.getLocalInstance().getUsersAPI();
        Call<Detail> call = usersAPI.removeBook(removeBook);
        call.enqueue(new Callback<Detail>() {
            @Override
            public void onResponse(Call<Detail> call, Response<Detail> response) {
                if(response.body() != null) {
                    bookList.remove(position);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Successfully removed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Detail> call, Throwable t) {
                Toast.makeText(context, "Check your network connectivity and try again", Toast.LENGTH_SHORT).show();
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
