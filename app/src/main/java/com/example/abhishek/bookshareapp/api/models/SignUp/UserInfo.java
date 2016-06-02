package com.example.abhishek.bookshareapp.api.models.SignUp;

import com.google.gson.annotations.SerializedName;

public class UserInfo {

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
}
