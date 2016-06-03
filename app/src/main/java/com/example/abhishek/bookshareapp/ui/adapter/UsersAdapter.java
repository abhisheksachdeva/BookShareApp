package com.example.abhishek.bookshareapp.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.example.abhishek.bookshareapp.R;
import com.example.abhishek.bookshareapp.api.NetworkingFactory;
import com.example.abhishek.bookshareapp.api.UsersAPI;
import com.example.abhishek.bookshareapp.api.models.Book;
import com.example.abhishek.bookshareapp.api.models.SignUp.UserInfo;
import com.example.abhishek.bookshareapp.ui.UserProfile;
import com.example.abhishek.bookshareapp.utils.Helper;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder>{

    private Context context;
    private List<UserInfo> userList;
    UserInfo tempValues=null;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
         void onItemClick(UserInfo userInfo);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView nameUser;
        public TextView emailUser;
        public TextView hostelUser;
        Context context;

        public ViewHolder(View v, Context context){
            super(v);
            nameUser = (TextView)v.findViewById(R.id.row_user_name);
            emailUser = (TextView)v.findViewById(R.id.row_user_email);
            hostelUser = (TextView)v.findViewById(R.id.row_user_hostel);
            this.context = context;
        }
    }

    public UsersAdapter(Context context, List<UserInfo> userList, OnItemClickListener listener){
        this.userList =userList;
        this.context=context;
        Log.d("UsersAdapter","Constructor");
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_users, parent, false);
        ViewHolder vh = new ViewHolder(v, context);
        return vh;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final String id;

        tempValues = userList.get(position);
        id = tempValues.getId();
        holder.nameUser.setText(tempValues.getName());
        holder.emailUser.setText(tempValues.getEmail());
        holder.hostelUser.setText(tempValues.getHostel());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(userList.get(position));
                Intent i = new Intent(context,UserProfile   .class);
                i.putExtra("id",id);
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        if(userList != null)
            return userList.size();

        return 0;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}
