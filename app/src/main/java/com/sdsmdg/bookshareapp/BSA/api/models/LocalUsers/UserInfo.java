package com.sdsmdg.bookshareapp.BSA.api.models.LocalUsers;

import com.google.gson.annotations.SerializedName;
import com.sdsmdg.bookshareapp.BSA.api.models.LocalBooks.Book;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

public class UserInfo extends RealmObject {

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

    public String getFcm_id() {
        return fcm_id;
    }

    public void setFcm_id(String fcm_id) {
        this.fcm_id = fcm_id;
    }

    @SerializedName("fcm_id")
    String fcm_id;

    @SerializedName("books")
    RealmList<Book> userBookList;

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getName() {
        return firstName + " " + lastName;
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

    public String getCollege() {
        return college;
    }

    public String getContactNo() {
        return contactNo;
    }

    public String getEnrNo() {
        return enrNo;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setEnrNo(String enrNo) {
        this.enrNo = enrNo;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setHostel(String hostel) {
        this.hostel = hostel;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public void setUserBookList(RealmList<Book> userBookList) {
        this.userBookList = userBookList;
    }
}
