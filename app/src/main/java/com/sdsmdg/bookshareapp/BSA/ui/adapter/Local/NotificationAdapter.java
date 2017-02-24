package com.sdsmdg.bookshareapp.BSA.ui.adapter.Local;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.api.NetworkingFactory;
import com.sdsmdg.bookshareapp.BSA.api.UsersAPI;
import com.sdsmdg.bookshareapp.BSA.api.models.Notification.Notifications;
import com.sdsmdg.bookshareapp.BSA.ui.BookDetailsActivity;
import com.sdsmdg.bookshareapp.BSA.ui.UserProfile;
import com.sdsmdg.bookshareapp.BSA.utils.Helper;

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
    SharedPreferences prefs;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView content, timeTextView;
        View accept, reject, buttonLayout;

        Context context;

        public ViewHolder(View v, Context context, int viewType) {
            super(v);
            content = (TextView) v.findViewById(R.id.content);
            timeTextView = (TextView) v.findViewById(R.id.time);
            buttonLayout = v.findViewById(R.id.button_layout);

            if (viewType == 1) {
                accept = v.findViewById(R.id.accept);
                reject = v.findViewById(R.id.reject);
            } else if (viewType == 2) {
                buttonLayout.setVisibility(View.GONE);
            }

            this.context = context;
        }
    }

    public NotificationAdapter(Context context, List<Notifications> list) {
        this.context = context;
        this.notificationList = list;
        prefs = context.getSharedPreferences("Token", Context.MODE_PRIVATE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_notification, parent, false);
        return new ViewHolder(v, context, viewType);
    }

    @Override
    public int getItemViewType(int position) {
        Notifications notifications = notificationList.get(position);
        if (notifications.getMessage().equals("requested for")) {
            return 1;
        }
        return 2;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        notifications = notificationList.get(position);

        int senderNameLength = notifications.getSenderName().length();
        int bookNameLength = notifications.getBookTitle().length();

        SpannableString content = null;
        final String bookId, notifId, targetId, bookTitle, time;
        Long timeDiff, minutes;
        Integer days, hours, weeks, months;

        timeDiff = System.currentTimeMillis() / 1000 - notifications.getUnix_time();
        minutes = timeDiff / 60;
        hours = minutes.intValue() / 60;
        days = hours / 24;
        weeks = days / 7;
        months = weeks / 4;

        if(timeDiff<0){
            timeDiff = Long.parseLong("0");
        }

        if (timeDiff < 60) {
            if (timeDiff == 1) {
                time = " " + timeDiff.toString() + " second ago";
            } else {
                time = " " + timeDiff.toString() + " seconds ago";
            }
        } else {
            if (minutes < 60) {
                if (minutes == 1) {
                    time = " " + minutes.toString() + " minute ago";
                } else {
                    time = " " + minutes.toString() + " minutes ago";
                }


            } else {
                if (hours < 25) {
                    if (hours == 1) {
                        time = " " + hours.toString() + " hour ago";

                    } else {
                        time = " " + hours.toString() + " hours ago";

                    }

                } else {
                    if (days == 1) {
                        time = " " + days.toString() + " day ago";

                    } else {
                        if (days < 7) {
                            time = " " + days.toString() + " days ago";
                        } else {
                            if (weeks == 1) {
                                time = " " + weeks.toString() + " week ago";

                            } else {
                                if (months < 1) {
                                    time = " " + weeks.toString() + " weeks ago";

                                } else {
                                    if (months == 1) {
                                        time = " " + months.toString() + " month ago";

                                    } else {
                                        time = " " + months.toString() + " months ago";

                                    }
                                }

                            }
                        }

                    }
                }
            }

        }


        String message = notifications.getMessage();

        if (message.equals("requested for")) {
            bookId = notifications.getBookId();
            bookTitle = notifications.getBookTitle();
            targetId = notifications.getSenderId();
            notifId = notifications.getId();

            content = new SpannableString(notifications.getSenderName() + " " + message + " " + notifications.getBookTitle());
            content.setSpan(getClickableSpanNameInstance(notifications.getSenderId()), 0, senderNameLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            content.setSpan(getClickableSpanBookInstance(notifications.getBookId()), senderNameLength + message.length() + 2, senderNameLength + message.length() + 2 + bookNameLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.timeTextView.setText(time);


            holder.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    acceptRequest(holder, notifId, bookId, bookTitle, Helper.getUserId(), Helper.getUserName(), targetId, v);
                }
            });

            holder.reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rejectRequest(holder, notifId);
                }
            });

        } else if (message.equals("You rejected request for")) {
            if (!notifications.getSenderId().equals(Helper.getUserId())) {
                String localMessage = "You rejected request by ";
                content = new SpannableString(localMessage + notifications.getSenderName() + " for " + notifications.getBookTitle());
                content.setSpan(getClickableSpanNameInstance(notifications.getSenderId()), localMessage.length(), localMessage.length() + senderNameLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                content.setSpan(getClickableSpanBookInstance(notifications.getBookId()), localMessage.length() + senderNameLength + 5, localMessage.length() + senderNameLength + 5 + bookNameLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.timeTextView.setText(time);

            }
        } else if (message.equals("has accepted your request for")) {
            content = new SpannableString(notifications.getSenderName() + " " + message + " " + notifications.getBookTitle());
            content.setSpan(getClickableSpanNameInstance(notifications.getSenderId()), 0, senderNameLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            content.setSpan(getClickableSpanBookInstance(notifications.getBookId()), senderNameLength + message.length() + 2, senderNameLength + message.length() + 2 + bookNameLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.timeTextView.setText(time);

        } else if (message.equals("You accepted request for")) {
            if (!notifications.getSenderId().equals(Helper.getUserId())) {
                String localMessage = "You accepted the request by ";
                content = new SpannableString(localMessage + notifications.getSenderName() + " for " + notifications.getBookTitle());
                content.setSpan(getClickableSpanNameInstance(notifications.getSenderId()), localMessage.length(), localMessage.length() + senderNameLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                content.setSpan(getClickableSpanBookInstance(notifications.getBookId()), localMessage.length() + senderNameLength + 5, localMessage.length() + senderNameLength + 5 + bookNameLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.timeTextView.setText(time);

            }
        }

        if (content != null) {
            holder.content.setText(content);
            holder.content.setMovementMethod(LinkMovementMethod.getInstance());
            holder.timeTextView.setText(time);

        } else {

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
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Are you sure you want to accept this request?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                UsersAPI usersAPI = NetworkingFactory.getLocalInstance().getUsersAPI();
                Call<Notifications> sendNotif = usersAPI.acceptNotif(nId, senderId, senderName, bookId, bookTitle, "accept", targetId, "accepted request", "Token " + prefs.getString("token", null));
                sendNotif.enqueue(new Callback<Notifications>() {
                    @Override
                    public void onResponse(Call<Notifications> call, Response<Notifications> response) {
                        if (response.body() != null) {
                            Toast.makeText(context, response.body().getDetail(), Toast.LENGTH_SHORT).show();
                            holder.buttonLayout.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(context, response.body().getDetail(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Notifications> call, Throwable t) {
                        Toast.makeText(context, R.string.connection_failed, Toast.LENGTH_SHORT).show();
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

    public void rejectRequest(final ViewHolder holder, final String nId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Are you sure you want to reject this request?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String process = "request";
                UsersAPI usersAPI = NetworkingFactory.getLocalInstance().getUsersAPI();
                Call<Notifications> sendNotif = usersAPI.rejectNotif(nId, "reject", "rejected request", "Token " + prefs.getString("token", null));
                sendNotif.enqueue(new Callback<Notifications>() {
                    @Override
                    public void onResponse(Call<Notifications> call, Response<Notifications> response) {
                        if (response.body() != null) {
                            Toast.makeText(context, response.body().getDetail(), Toast.LENGTH_SHORT).show();
                            holder.buttonLayout.setVisibility(View.GONE);

                        } else {
                            Toast.makeText(context, response.body().getDetail(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Notifications> call, Throwable t) {
                        Toast.makeText(context, R.string.connection_failed, Toast.LENGTH_SHORT).show();
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

    @Override
    public int getItemCount() {
        if (notificationList != null)
            return notificationList.size();
        return 0;
    }
}