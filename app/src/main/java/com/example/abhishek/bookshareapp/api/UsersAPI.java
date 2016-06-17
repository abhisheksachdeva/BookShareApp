package com.example.abhishek.bookshareapp.api;

import com.example.abhishek.bookshareapp.api.models.LocalBooks.Book;
import com.example.abhishek.bookshareapp.api.models.Login;
import com.example.abhishek.bookshareapp.api.models.Notification.Notifications;
import com.example.abhishek.bookshareapp.api.models.Signup;
import com.example.abhishek.bookshareapp.api.models.UserInfo;
import com.example.abhishek.bookshareapp.api.models.VerifyToken.UserEmail;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface UsersAPI {

    @FormUrlEncoded
    @POST("users/reg/?format=json")
    Call<Signup> getUserInfo(
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

    @GET("book/{id}/?format=json")
    Call<Book> getBookDetails(
            @Path("id") String id
    );

    @GET("user/{id}/?format=json")
    Call<UserInfo> getUserDetails(
            @Path("id") String id
    );

    @POST("token/")
    Call<UserEmail> getUserEmail();

    @GET("notifications/")
    Call<List<Notifications>> getNotifs(
            @Query("user_id") String userId
    );

    @FormUrlEncoded
    @POST("notifications/")
    Call<Notifications> sendNotif(
            @Field("sender_id") String senderId,
            @Field("sender_name") String senderName,
            @Field("book_id") String bookId,
            @Field("book_title") String bookTitle,
            @Field("process") String process,
            @Field("target_id") String targetId,
            @Field("message") String message
    );

    @FormUrlEncoded
    @POST("notifications/")
    Call<Notifications> acceptNotif(
            @Field("notif_id") String  notifId,
            @Field("sender_id") String senderId,
            @Field("sender_name") String senderName,
            @Field("book_id") String bookId,
            @Field("book_title") String bookTitle,
            @Field("process") String process,
            @Field("target_id") String targetId,
            @Field("message") String message
    );

    @FormUrlEncoded
    @POST("notifications/")
    Call<Notifications> rejectNotif(
            @Field("notif_id") String  notifId,
            @Field("process") String process,
            @Field("message") String message
    );

    @PUT("user/{id}/")
    Call<UserInfo> editUserDetails(
            @Path("id") String id,
            @Body UserInfo userInfo
    );

}
