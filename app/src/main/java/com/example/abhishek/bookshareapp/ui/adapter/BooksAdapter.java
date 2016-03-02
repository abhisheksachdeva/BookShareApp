package com.example.abhishek.bookshareapp.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.abhishek.bookshareapp.R;
import com.example.abhishek.bookshareapp.api.models.Book;
import com.example.abhishek.bookshareapp.ui.SearchResultsActivity;

import java.util.List;

/**
 * Created by abhishek on 30/1/16.
 */
public class BooksAdapter extends BaseAdapter implements View.OnClickListener {

    private Context context;
    private List<Book> bookList;
    private static LayoutInflater inflater=null;
    Book tempValues=null;

    public BooksAdapter(Context context,List<Book> bookList){
        this.bookList =bookList;
        this.context=context;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Log.d("BookAdapter","Constructor");
    }

    @Override
    public int getCount() {

        return bookList.size();
    }

    @Override
    public Object getItem(int position) {
        return bookList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return bookList.get(position).getISBN();
    }

    public static class ViewHolder{
        public TextView title_book;
        public TextView author_book;
        public ImageView image_book;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("BookAdapter","getView");
        View vi = convertView;
        ViewHolder holder;
        if(convertView == null){
            vi = inflater.inflate(R.layout.row_books,null);

            holder = new ViewHolder();
            holder.author_book = (TextView)vi.findViewById(R.id.row_books_author);
            holder.title_book = (TextView)vi.findViewById(R.id.row_books_title);
            holder.image_book = (ImageView)vi.findViewById(R.id.row_books_imageView);

            vi.setTag(holder);
        }
        else{
            holder=(ViewHolder)vi.getTag();
        }
        if(bookList.size()<=0) {
            holder.title_book.setText("No Data");
        }
        else{
            tempValues=null;
            tempValues=bookList.get(position);

            holder.title_book.setText(tempValues.getVolumeInfo().getTitle());
            holder.author_book.setText(tempValues.getVolumeInfo().getAllAuthors());

            vi.setOnClickListener(new OnItemClickListener(position));
        }

        return vi;
    }

    @Override
    public void onClick(View v) {
        Log.v("CustomAdapter", "=====Row button clicked=====");
    }

    private class OnItemClickListener  implements View.OnClickListener {
        private int mPosition;

        OnItemClickListener(int position){
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {


            SearchResultsActivity sct = (SearchResultsActivity)context;

            /****  Call  onItemClick Method inside CustomListViewAndroidExample Class ( See Below )****/

            sct.onItemClick(mPosition);
        }
    }
}
