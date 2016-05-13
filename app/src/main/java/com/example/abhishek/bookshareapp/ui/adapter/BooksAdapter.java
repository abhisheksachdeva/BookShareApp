package com.example.abhishek.bookshareapp.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.abhishek.bookshareapp.R;
import com.example.abhishek.bookshareapp.api.models.Book;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.ViewHolder>{

    private Context context;
    private List<Book> bookList;
    Book tempValues=null;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        public void onItemClick(Book book);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView titleBook;
        public TextView authorBook;
        public ImageView imageBook;
        public RatingBar ratingBook;
        Context context;

        public ViewHolder(View v, Context context){
            super(v);
            titleBook = (TextView)v.findViewById(R.id.row_books_title);
            authorBook = (TextView)v.findViewById(R.id.row_books_author);
            imageBook = (ImageView) v.findViewById(R.id.row_books_imageView);
            ratingBook = (RatingBar) v.findViewById(R.id.row_books_rating);
            this.context = context;
        }

    }

    public BooksAdapter(Context context, List<Book> bookList, OnItemClickListener listener){
        this.bookList =bookList;
        this.context=context;
        Log.d("BookAdapter","Constructor");
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_books, parent, false);

        ViewHolder vh = new ViewHolder(v, context);

        return vh;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        tempValues = bookList.get(position);

        holder.titleBook.setText(tempValues.getBookDetails().getTitle());
        holder.authorBook.setText(tempValues.getBookDetails().getAuthor().getAuthor_name());
        Picasso.with(this.context).load(tempValues.getBookDetails().getImage_url()).into(holder.imageBook);
        holder.ratingBook.setRating(tempValues.getRating());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(bookList.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        if(bookList != null)
            return bookList.size();

        return 0;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}
