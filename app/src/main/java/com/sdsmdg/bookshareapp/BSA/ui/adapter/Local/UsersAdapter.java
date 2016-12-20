package com.sdsmdg.bookshareapp.BSA.ui.adapter.Local;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.api.NetworkingFactory;
import com.sdsmdg.bookshareapp.BSA.api.UsersAPI;
import com.sdsmdg.bookshareapp.BSA.api.models.Notification.Notifications;
import com.sdsmdg.bookshareapp.BSA.api.models.UserInfo;
import com.sdsmdg.bookshareapp.BSA.ui.MyProfile;
import com.sdsmdg.bookshareapp.BSA.ui.UserProfile;
import com.sdsmdg.bookshareapp.BSA.utils.CommonUtilities;
import com.sdsmdg.bookshareapp.BSA.utils.Helper;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private Context context;
    private List<UserInfo> userList;
    UserInfo tempValues = null;
    private final OnItemClickListener listener;
    String bookId, bookTitle;
    String userId;
    boolean withRequestButton;
    SharedPreferences prefs ;
    public interface OnItemClickListener {
        void onItemClick(UserInfo userInfo);
    }

    @Override
    public int getItemViewType(int position) {
        if (withRequestButton) {
            return 0;
        }
        return 1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameUser;
        public TextView emailUser;
        public Button request;
        public TextView hostelUser;
        public ImageView imageUser;
        Context context;

        public ViewHolder(View v, Context context, int viewType) {
            super(v);
            nameUser = (TextView) v.findViewById(R.id.row_user_name);
            imageUser = (ImageView) v.findViewById(R.id.row_user_image);

            emailUser = (TextView) v.findViewById(R.id.row_user_email);
            hostelUser = (TextView) v.findViewById(R.id.row_user_hostel);
            if (viewType == 0)
                request = (Button) v.findViewById(R.id.requestButton);

            this.context = context;
        }
    }

    public UsersAdapter(boolean withRequestButton, String userId, Context context, List<UserInfo> userList, String bookTitle, String bookId, OnItemClickListener listener) {
        this.userList = userList;
        this.context = context;
        this.listener = listener;
        this.bookTitle = bookTitle;
        this.bookId = bookId;
        this.userId = userId;
        this.withRequestButton = withRequestButton;
        prefs = context.getSharedPreferences("Token",Context.MODE_PRIVATE);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (viewType == 0) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_users, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_users_without_request, parent, false);
        }
        return new ViewHolder(v, context, viewType);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final String id;
        tempValues = userList.get(position);
        id = tempValues.getId();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(userList.get(position));
                Intent i;
                if (!userId.equals(id)) {//Open UserProfile if it is not me
                    i = new Intent(context, UserProfile.class);
                    i.putExtra("id", id);
                } else {//Open MyProfile if it's me
                    i = new Intent(context, MyProfile.class);
                    i.putExtra("id", id);
                }
                context.startActivity(i);
            }
        });

        holder.nameUser.setText(tempValues.getName());
        holder.emailUser.setText(tempValues.getEmail());
        holder.hostelUser.setText(tempValues.getHostel());

        if (!id.equals(userId)) {
            if (withRequestButton) {
                holder.request.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAlertDialog(holder, id);
                    }
                });
            }
        }else {
            if(withRequestButton){
                holder.request.setVisibility(View.GONE);

            }
        }

        try {
            String url = CommonUtilities.local_books_api_url + "image/" + id + "/";
            Picasso.with(this.context).load(url).placeholder(R.drawable.ic_account_circle_black_24dp).into(holder.imageUser);
        } catch (Exception e) {
            Toast.makeText(this.context, e.toString(), Toast.LENGTH_SHORT).show();

        }


    }

    public void showAlertDialog(final ViewHolder holder, final String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Send request?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String process = "request";
                UsersAPI usersAPI = NetworkingFactory.getLocalInstance().getUsersAPI();
                Call<Notifications> sendNotif = usersAPI.sendNotif(Helper.getUserId(), Helper.getUserName(), bookId, bookTitle, process, id, "request for","Token "+prefs.getString("token",null));
                sendNotif.enqueue(new Callback<Notifications>() {
                    @Override
                    public void onResponse(Call<Notifications> call, Response<Notifications> response) {
                        if (response.body() != null) {
                            Toast.makeText(context, response.body().getDetail(), Toast.LENGTH_SHORT).show();
                            holder.request.setEnabled(false);

                        } else {
                            Toast.makeText(context, response.body().getDetail(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Notifications> call, Throwable t) {
                        Toast.makeText(context, "Check your internet connection and try again!", Toast.LENGTH_SHORT).show();
                    }
                });
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

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    @Override
    public int getItemCount() {
        if (userList != null)
            return userList.size();

        return 0;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
