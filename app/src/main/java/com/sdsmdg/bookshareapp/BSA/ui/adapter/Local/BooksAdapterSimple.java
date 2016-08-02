package com.sdsmdg.bookshareapp.BSA.ui.adapter.Local;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.abhishek.bookshareapp.R;
import com.sdsmdg.bookshareapp.BSA.api.models.LocalBooks.Book;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BooksAdapterSimple extends RecyclerView.Adapter<BooksAdapterSimple.ViewHolder> {

    private Context context;
    private List<Book> bookList;
    Book tempValues = null;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        public void onItemClick(Book book);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleBook;
        public TextView authorBook;
        public ImageView imageBook;
        public RatingBar ratingBook;
        public TextView ratingCount;
        Context context;

        public ViewHolder(View v, Context context) {
            super(v);
            titleBook = (TextView) v.findViewById(R.id.row_books_title);
            authorBook = (TextView) v.findViewById(R.id.row_books_author);
            imageBook = (ImageView) v.findViewById(R.id.row_books_imageView);
            ratingBook = (RatingBar) v.findViewById(R.id.row_books_rating);
            ratingCount = (TextView) v.findViewById(R.id.row_books_ratings_count);
            this.context = context;
        }

    }

    public BooksAdapterSimple(Context context, List<Book> bookList, OnItemClickListener listener) {
        this.bookList = bookList;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_books_simple, parent, false);
        ViewHolder vh = new ViewHolder(v, context);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        tempValues = bookList.get(position);

        holder.titleBook.setText(tempValues.getTitle());
        holder.authorBook.setText(tempValues.getAuthor());
        if(!tempValues.getGrImgUrl().isEmpty()) {
            Picasso.with(this.context).load(tempValues.getGrImgUrl()).placeholder(R.drawable.default_book_image).into(holder.imageBook);
        }
        holder.ratingBook.setRating(tempValues.getRating());
        holder.ratingCount.setText(tempValues.getRatingsCount() + " votes");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(bookList.get(position));
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
