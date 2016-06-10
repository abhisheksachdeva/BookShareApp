package com.example.abhishek.bookshareapp.api.models.Notification;
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
    String isConfirmed;

    @SerializedName("is_cancelled")
    String isCancelled;

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

    public String getIsConfirmed() {
        return isConfirmed;
    }

    public String getIsCancelled() {
        return isCancelled;
    }
}
