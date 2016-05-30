package com.example.abhishek.bookshareapp.api;

import com.example.abhishek.bookshareapp.api.models.LocalBooks.Book;
import com.example.abhishek.bookshareapp.api.models.Login;
import com.example.abhishek.bookshareapp.api.models.SignUp.UserInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface UsersAPI {

    @FormUrlEncoded
    @POST("users/reg/?format=json")
    Call<UserInfo> getUserInfo(
            @Field("email") String email,
            @Field("college") String college,
            @Field("hostel") String hostel,
            @Field("room_no") String roomNo,
            @Field("enr_no") String enrNo,
            @Field("first_name") String firstName,
            @Field("last_name") String lastName,
            @Field("contact_no") String contactNo,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("login/")
    Call<Login> getToken(
            @Field("email") String email,
            @Field("password") String password
    );

    @GET("books/?format=json")
    Call<List<Book>> getBooksList();

    @FormUrlEncoded
    @POST("books/?format=json")
    Call<Book> addBook(
            @Field("email") String email,
            @Field("title") String title,
            @Field("author") String author,
            @Field("gr_id") String gr_id,
            @Field("ratings_count") Long ratingsCount,
            @Field("rating") Float rating,
            @Field("gr_img_url") String gr_img_url



            );

}
