package com.example.abhishek.bookshareapp.ui.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhishek.bookshareapp.R;
import com.example.abhishek.bookshareapp.api.NetworkingFactory;
import com.example.abhishek.bookshareapp.api.UsersAPI;
import com.example.abhishek.bookshareapp.api.models.LocalBooks.Book;
import com.example.abhishek.bookshareapp.api.models.Notification.Notifications;
import com.example.abhishek.bookshareapp.api.models.UserInfo;
import com.example.abhishek.bookshareapp.ui.UserProfile;
import com.example.abhishek.bookshareapp.utils.Helper;

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
        public Button request;
        public TextView hostelUser;
        Context context;

        public ViewHolder(View v, Context context){
            super(v);
            nameUser = (TextView)v.findViewById(R.id.row_user_name);
            emailUser = (TextView)v.findViewById(R.id.row_user_email);
            hostelUser = (TextView)v.findViewById(R.id.row_user_hostel);
            request =(Button)v.findViewById(R.id.request);
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
                Intent i = new Intent(context,UserProfile.class);
                i.putExtra("id",id);
                context.startActivity(i);
            }
        });

        holder.request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CharSequence[] items = { "Yes", "No"};
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Do you want to send a request?");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (items[which].equals("Yes")){
                            String process = "request";
                            UsersAPI usersAPI = NetworkingFactory.getLocalInstance().getUsersAPI();
                            Call<Notifications> sendNotif = usersAPI.sendNotif(Helper.getUserId(),Helper.getUserName(),Helper.getBookId(),Helper.getBookTitle() ,process,id);
                            sendNotif.enqueue(new Callback<Notifications>() {
                                @Override
                                public void onResponse(Call<Notifications> call, Response<Notifications> response) {
                                    Log.i("Email iD ", Helper.getUserEmail());
                                    if (response.body() != null) {
                                        Log.i("SendNotif", "Success");
                                        Log.d("SendNotif", Helper.getUserId()+" ID"+id);
                                        Toast.makeText(context, response.body().getDetail(), Toast.LENGTH_SHORT).show();
                                        Log.i("response", response.body().getDetail());
                                        holder.request.setEnabled(false);

                                    } else {
                                        Log.i("SendNotif", "Response Null");
                                        Toast.makeText(context, response.body().getDetail() , Toast.LENGTH_SHORT).show();
                                    }
                                }
                                @Override
                                public void onFailure(Call<Notifications> call, Throwable t) {
                                    Log.i("SendNotif","Failed!!");
                                }
                            });
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
