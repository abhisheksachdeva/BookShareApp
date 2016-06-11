package com.example.abhishek.bookshareapp.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.abhishek.bookshareapp.R;
import com.example.abhishek.bookshareapp.api.models.Notification.Notifications;
import com.example.abhishek.bookshareapp.utils.Helper;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    Context context;
    List<Notifications> notificationList = new ArrayList<>();
    Notifications notifications = null;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView content;
        public Button accept;
        public Button reject;

        Context context;

        public ViewHolder(View v, Context context) {
            super(v);
            content = (TextView) v.findViewById(R.id.content);
            accept = (Button) v.findViewById(R.id.accept);
            reject = (Button) v.findViewById(R.id.reject);

            this.context = context;
        }
    }

    public NotificationAdapter(Context context, List<Notifications> list) {
        this.context = context;
        this.notificationList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_notification, parent, false);

        ViewHolder vh = new ViewHolder(v, context);

        return vh;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        String content = null;

        holder.accept.setVisibility(View.INVISIBLE);
        holder.accept.setEnabled(false);

        holder.reject.setVisibility(View.INVISIBLE);
        holder.reject.setEnabled(false);

        notifications = notificationList.get(position);
        Log.i("NotifAdap","dffsf");
        Log.i("NotifAdap",notificationList.get(position).getMessage());
        if (notifications.getMessage().equals("requested for")) {
            content = notifications.getSenderName() + " requested for " + notifications.getBookTitle() + "\n";
            holder.accept.setEnabled(true);
            holder.accept.setVisibility(View.VISIBLE);
            holder.reject.setEnabled(true);
            holder.reject.setVisibility(View.VISIBLE);
            holder.accept.setText("Accept");
            holder.reject.setText("Reject");

            holder.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    acceptRequest();
                }
            });

            holder.reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rejectRequest();
                }
            });
        } else if (notifications.getMessage().equals("You rejected request for")) {
            if (!notifications.getSenderId().equals(Helper.getUserId())) {
                content = notifications.getSenderName() + " rejected your request for " + notifications.getBookTitle();
            }
        } else if (notifications.getMessage().equals("has accepted your request for")) {
            content = notifications.getSenderName() + " " + notifications.getMessage() + " " + notifications.getBookTitle() + "\n";
        }

        if (content != null) {
            holder.content.setText(content);
        } else {
            Log.i("Notif_Adapter", "content == null");
        }

    }


    public void acceptRequest() {
        //Implement accept request logic here
        //If you need more variables than get it like I got it in BookDetails(don't use Helper class)
    }

    public void rejectRequest() {
        //Implement reject request logic here
    }

    @Override
    public int getItemCount() {
        if (notificationList != null)
            return notificationList.size();
        return 0;
    }
}