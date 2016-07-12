package com.example.abhishek.bookshareapp.ui.adapter.Local;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhishek.bookshareapp.R;
import com.example.abhishek.bookshareapp.api.NetworkingFactory;
import com.example.abhishek.bookshareapp.api.UsersAPI;
import com.example.abhishek.bookshareapp.api.models.Notification.Notifications;
import com.example.abhishek.bookshareapp.ui.BookDetailsActivity;
import com.example.abhishek.bookshareapp.ui.UserProfile;
import com.example.abhishek.bookshareapp.utils.Helper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    final String TAG = NotificationAdapter.class.getSimpleName();

    Context context;
    List<Notifications> notificationList = new ArrayList<>();
    Notifications notifications = null;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView content;
        View accept, reject, buttonLayout;

        Context context;

        public ViewHolder(View v, Context context, int viewType) {
            super(v);
            content = (TextView) v.findViewById(R.id.content);

            if(viewType == 1) {
                buttonLayout = v.findViewById(R.id.button_layout);
                accept = v.findViewById(R.id.accept);
                reject = v.findViewById(R.id.reject);
            }

            this.context = context;
        }
    }

    public NotificationAdapter(Context context, List<Notifications> list) {
        this.context = context;
        this.notificationList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = null;

        switch (viewType) {
            case 1:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_notification, parent, false);
                break;
            case 2:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_notification_without_button, parent, false);
        }

        return new ViewHolder(v, context, viewType);
    }

    @Override
    public int getItemViewType(int position) {
        Notifications notifications = notificationList.get(position);
        if(notifications.getMessage().equals("requested for")) {
            return 1;
        }
        return 2;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        notifications = notificationList.get(position);
        Log.i("NotifAdap", notificationList.get(position).getMessage());

        int senderNameLength = notifications.getSenderName().length();
        int bookNameLength = notifications.getBookTitle().length();

        SpannableString content = null;
        final String bookId, notifId, targetId, bookTitle;

        if (notifications.getMessage().equals("requested for")) {
            content = new SpannableString(notifications.getSenderName() + " requested for " + notifications.getBookTitle()) ;
            content.setSpan(getClickableSpanNameInstance(notifications.getSenderId()), 0, senderNameLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            content.setSpan(getClickableSpanBookInstance(notifications.getBookId()), senderNameLength + 15, senderNameLength + 15 + bookNameLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            bookId = notifications.getBookId();
            bookTitle = notifications.getBookTitle();
            targetId = notifications.getSenderId();
            notifId = notifications.getId();

            holder.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    acceptRequest( holder, notifId, bookId, bookTitle, Helper.getUserId(), Helper.getUserName(), targetId, v);
                }
            });

            holder.reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rejectRequest( holder, notifId);
                }
            });

        } else if (notifications.getMessage().equals("You rejected request for")) {
            if (!notifications.getSenderId().equals(Helper.getUserId())) {
                content = new SpannableString("You rejected your request by " + notifications.getSenderName() + " for " + notifications.getBookTitle());
                content.setSpan(getClickableSpanNameInstance(notifications.getSenderId()), 29, 29 + senderNameLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                content.setSpan(getClickableSpanBookInstance(notifications.getBookId()), 29 + senderNameLength + 5, 29 + senderNameLength + 5 + bookNameLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } else if (notifications.getMessage().equals("has accepted your request for")) {
            content = new SpannableString(notifications.getSenderName() + " " + notifications.getMessage() + " " + notifications.getBookTitle());
            content.setSpan(getClickableSpanNameInstance(notifications.getSenderId()), 0, senderNameLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            content.setSpan(getClickableSpanBookInstance(notifications.getBookId()), senderNameLength + 31, senderNameLength + 31 + bookNameLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (notifications.getMessage().equals("You accepted request for")) {
            if (!notifications.getSenderId().equals(Helper.getUserId())) {
                content = new SpannableString("You accepted the request by " + notifications.getSenderName() + " for " + notifications.getBookTitle());
                content.setSpan(getClickableSpanNameInstance(notifications.getSenderId()), 28, 28 + senderNameLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                content.setSpan(getClickableSpanBookInstance(notifications.getBookId()), 28 + senderNameLength + 5, 28 + senderNameLength + 5 + bookNameLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        if (content != null) {
            holder.content.setText(content);
            holder.content.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            Log.i("Notif_Adapter", "content == null");
        }

    }

    private ClickableSpan getClickableSpanNameInstance(final String id) {
        return new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent i = new Intent(context, UserProfile.class);
                i.putExtra("id", id);
                context.startActivity(i);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                ds.setColor(Color.BLACK);
            }
        };
    }

    private ClickableSpan getClickableSpanBookInstance(final String id) {
        return new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent i = new Intent(context, BookDetailsActivity.class);
                i.putExtra("id", id);
                context.startActivity(i);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
                ds.setColor(Color.argb(255, 61, 61, 61));
            }
        };
    }

    public void acceptRequest(final ViewHolder holder, final String nId, final String bookId, final String bookTitle, final String senderId, final String senderName, final String targetId, final View v) {
        final CharSequence[] items = {"Yes", "No"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Are you sure you want to accept this request?");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (items[which].equals("Yes")) {
                    UsersAPI usersAPI = NetworkingFactory.getLocalInstance().getUsersAPI();
                    Call<Notifications> sendNotif = usersAPI.acceptNotif(nId, senderId, senderName, bookId, bookTitle, "accept", targetId, "accepted request");
                    sendNotif.enqueue(new Callback<Notifications>() {
                        @Override
                        public void onResponse(Call<Notifications> call, Response<Notifications> response) {
                            if (response.body() != null) {
                                Log.i("AcceptNotif", "Success");
                                Toast.makeText(context, response.body().getDetail(), Toast.LENGTH_SHORT).show();
                                Log.i("response", response.body().getDetail());
                                holder.buttonLayout.setVisibility(View.GONE);
                            } else {
                                Log.i("AccpetNotif", "Response Null");
                                Toast.makeText(context, response.body().getDetail(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Notifications> call, Throwable t) {
                            Log.i("AcceptNotif", "Failed!!");
                            Toast.makeText(context, "Check your internet connection and try again!", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void rejectRequest(final ViewHolder holder, final String nId) {
        final CharSequence[] items = {"Yes", "No"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Are you sure you want to reject this request?");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (items[which].equals("Yes")) {
                    String process = "request";
                    UsersAPI usersAPI = NetworkingFactory.getLocalInstance().getUsersAPI();
                    Call<Notifications> sendNotif = usersAPI.rejectNotif(nId, "reject", "rejected request");
                    sendNotif.enqueue(new Callback<Notifications>() {
                        @Override
                        public void onResponse(Call<Notifications> call, Response<Notifications> response) {
                            if (response.body() != null) {
                                Log.i("RejectNotif", "Success");
                                Toast.makeText(context, response.body().getDetail(), Toast.LENGTH_SHORT).show();
                                Log.i("response", response.body().getDetail());
                                holder.buttonLayout.setVisibility(View.GONE);

                            } else {
                                Log.i("rejectNotif", "Response Null");
                                Toast.makeText(context, response.body().getDetail(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Notifications> call, Throwable t) {
                            Log.i("rejectNotif", "Failed!!");
                            Toast.makeText(context, "Check your internet connection and try again!", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public int getItemCount() {
        if (notificationList != null)
            return notificationList.size();
        return 0;
    }
}