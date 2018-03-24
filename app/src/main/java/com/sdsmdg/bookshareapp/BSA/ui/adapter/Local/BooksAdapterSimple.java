package com.sdsmdg.bookshareapp.BSA.ui.adapter.Local;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.api.models.LocalBooks.Book;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

public class BooksAdapterSimple extends RecyclerView.Adapter<BooksAdapterSimple.ViewHolder> {

    private static final int VIEW_TYPE_LOADING = 123;
    private Context context;
    private List<Book> bookList;
    Book tempValues = null;
    private final OnItemClickListener listener;
    private int totalCount = 0;

    public interface OnItemClickListener {
        public void onItemClick(Book book);
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

    public BooksAdapterSimple(Context context, List<Book> bookList, OnItemClickListener listener) {
        this.bookList = bookList;
        this.context = context;
        this.listener = listener;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder vh;
        if(viewType == VIEW_TYPE_LOADING) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.progress_item, parent, false);
            vh = new ViewHolder(v, context);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_books_simple, parent, false);
            vh = new ViewHolder(v, context);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        if(position < bookList.size()) {
            tempValues = bookList.get(position);

            holder.titleBook.setText(tempValues.getTitle());
            holder.authorBook.setText(tempValues.getAuthor());
            if (!tempValues.getGrImgUrl().isEmpty()) {
                Picasso.with(this.context).load(tempValues.getGrImgUrl()).placeholder(R.drawable.default_book_image).into(holder.imageBook);
            }
            holder.ratingBook.setRating(tempValues.getRating());
            DecimalFormat formatter = new DecimalFormat("#,###,###");
            String rating_count = formatter.format(tempValues.getRatingsCount());
            holder.ratingCount.setText("(" + rating_count + ")");

            holder.link.setVisibility(View.GONE);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(bookList.get(position));
                }
            });
        } else {
            //It means the progress bar is shown on screen
        }

    }

    @Override
    public int getItemCount() {
        if (bookList != null)
            return bookList.size();

        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if(position >= bookList.size() && totalCount > bookList.size()) {
            return VIEW_TYPE_LOADING;
        }
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}
