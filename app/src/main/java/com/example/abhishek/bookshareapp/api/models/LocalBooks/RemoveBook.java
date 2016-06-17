package com.example.abhishek.bookshareapp.api.models.LocalBooks;

import com.google.gson.annotations.SerializedName;

public class RemoveBook {

    @SerializedName("user_id")
    String userId;

    @SerializedName("book_id")
    String bookId;

    public String getBookId() {
        return bookId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }
}
