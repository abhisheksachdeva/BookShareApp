package com.sdsmdg.bookshareapp.BSA.api.models.Notification;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ajayrahul on 9/6/16.
 */
public class Notifications {

    String id;
    String detail;

    @SerializedName("sender_id")
    String senderId;

    @SerializedName("sender_name")
    String senderName;

    @SerializedName("book_id")
    String bookId;

    @SerializedName("book_title")
    String bookTitle;

    String message;

    @SerializedName("is_confirmed")
    boolean isConfirmed;

    @SerializedName("is_owner")
    String isOwner;

    @SerializedName("is_cancelled")
    boolean isCancelled;

    @SerializedName("unix_time")
    long unix_time;

    public long getUnix_time() {
        return unix_time;
    }

    public String getDetail() {
        return detail;
    }

    public String getId() {
        return id;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getBookId() {
        return bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getMessage() {
        return message;
    }

    public boolean getIsConfirmed() {
        return isConfirmed;
    }

    public boolean getIsCancelled() {
        return isCancelled;
    }
}
