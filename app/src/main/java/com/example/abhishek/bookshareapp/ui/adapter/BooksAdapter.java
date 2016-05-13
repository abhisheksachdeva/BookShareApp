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
import android.widget.Toast;

import com.example.abhishek.bookshareapp.R;
import com.example.abhishek.bookshareapp.api.models.Book;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.ViewHolder>{

    private Context context;
    private List<Book> bookList;
    private static LayoutInflater inflater=null;
    Book tempValues=null;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView titleBook;
        public TextView authorBook;
        public ImageView imageBook;
        public RatingBar ratingBook;
        public TextView ratingCount;
        Context context;

        public ViewHolder(View v, Context context){
            super(v);
            titleBook = (TextView)v.findViewById(R.id.row_books_title);
            authorBook = (TextView)v.findViewById(R.id.row_books_author);
            imageBook = (ImageView) v.findViewById(R.id.row_books_imageView);
            ratingBook = (RatingBar) v.findViewById(R.id.row_books_rating);
            ratingCount= (TextView) v.findViewById(R.id.ratings_count);
            titleBook.setOnClickListener(this);
            authorBook.setOnClickListener(this);
            imageBook.setOnClickListener(this);
            ratingBook.setOnClickListener(this);
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(context,"Clicked",Toast.LENGTH_SHORT).show();
        }
    }

    public BooksAdapter(Context context,List<Book> bookList){
        this.bookList =bookList;
        this.context=context;
        Log.d("BookAdapter","Constructor");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_books, parent, false);

        ViewHolder vh = new ViewHolder(v, context);

        return vh;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        tempValues = bookList.get(position);

        holder.titleBook.setText(tempValues.getBookDetails().getTitle());
        holder.authorBook.setText(tempValues.getBookDetails().getAuthor().getAuthor_name());
        Picasso.with(this.context).load(tempValues.getBookDetails().getImage_url()).into(holder.imageBook);
        holder.ratingBook.setRating(tempValues.getRating());
        holder.ratingCount.setText(tempValues.getRatingCount());

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
