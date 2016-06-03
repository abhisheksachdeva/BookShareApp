package com.example.abhishek.bookshareapp.api.models.SignUp;

import com.example.abhishek.bookshareapp.api.models.LocalBooks.Book;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserInfo {

    String id;



    String email;
    String college;
    String hostel;
    @SerializedName("enr_no")
    String enrNo;
    @SerializedName("room_no")
    String roomNo;
    @SerializedName("first_name")
    String firstName;
    @SerializedName("last_name")
    String lastName;
    @SerializedName("contact_no")
    String contactNo;

    @SerializedName("books")
    List<Book> userBookList;

    public String getEmail() {
        return email;
    }

    public String getFirstName(){
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getName() {
        return firstName +" "+ lastName;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public String getId() {
        return id;
    }

    public String getHostel() {

        return hostel;
    }

    public List<Book> getUserBookList() {
        return userBookList;
    }
}
