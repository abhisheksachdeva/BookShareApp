package com.sdsmdg.bookshareapp.BSA.ui.adapter.GR;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.api.models.BookDetailsToRead;
import com.sdsmdg.bookshareapp.BSA.ui.MainActivity;
import com.sdsmdg.bookshareapp.BSA.ui.SearchResultsActivity;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

public class BooksAdapterToRead extends RecyclerView.Adapter<BooksAdapterToRead.ViewHolder> {

    private Context context;
    private List<BookDetailsToRead> bookDetailsToReads;
    BookDetailsToRead tempValues = null;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        public void onItemClick(BookDetailsToRead book);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleBook;
        public TextView authorBook;
        public ImageView imageBook;
        public RatingBar ratingBook;
        public TextView ratingCount;
        public TextView link;
        Context context;

        public ViewHolder(View v, Context context) {
            super(v);
            titleBook = (TextView) v.findViewById(R.id.row_books_title);
            authorBook = (TextView) v.findViewById(R.id.row_books_author);
            imageBook = (ImageView) v.findViewById(R.id.row_books_imageView);
            ratingBook = (RatingBar) v.findViewById(R.id.row_books_rating);
            ratingCount = (TextView) v.findViewById(R.id.row_books_ratings_count);
            link = (TextView) v.findViewById(R.id.directSearch);
            this.context = context;
        }

    }

    public BooksAdapterToRead(Context context, List<BookDetailsToRead> bookDetailsToReadList, OnItemClickListener listener) {
        this.bookDetailsToReads = bookDetailsToReadList;
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
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        tempValues = bookDetailsToReads.get(position);

        holder.titleBook.setText(tempValues.getTitle());
        holder.authorBook.setText(tempValues.getAuthor().getAuthors().getAuthor_name());
        if (!tempValues.getImage_url().isEmpty()) {
            Picasso.with(this.context).load(tempValues.getImage_url()).placeholder(R.drawable.default_book_image).into(holder.imageBook);
        }
        holder.ratingBook.setRating(tempValues.getRating());
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        String rating_count = formatter.format(Long.parseLong(tempValues.getRatingCount().toString()));
        holder.ratingCount.setText("(" + rating_count + ")");

        holder.link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String searchItem = holder.titleBook.getText().toString();

                //Toast.makeText(context, searchItem, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("pass_it_on", searchItem);
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        if (bookDetailsToReads != null)
            return bookDetailsToReads.size();

        return 0;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}
