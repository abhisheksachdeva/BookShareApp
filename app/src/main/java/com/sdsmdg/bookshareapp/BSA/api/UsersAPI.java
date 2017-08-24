package com.sdsmdg.bookshareapp.BSA.api;

import com.sdsmdg.bookshareapp.BSA.api.models.LocalBooks.Book;
import com.sdsmdg.bookshareapp.BSA.api.models.LocalBooks.BookDetailWithCancel;
import com.sdsmdg.bookshareapp.BSA.api.models.LocalBooks.BookList;
import com.sdsmdg.bookshareapp.BSA.api.models.LocalBooks.RemoveBook;
import com.sdsmdg.bookshareapp.BSA.api.models.LocalUsers.UserDetailWithCancel;
import com.sdsmdg.bookshareapp.BSA.api.models.Login;
import com.sdsmdg.bookshareapp.BSA.api.models.Notification.Notification_Model;
import com.sdsmdg.bookshareapp.BSA.api.models.Notification.Notifications;
import com.sdsmdg.bookshareapp.BSA.api.models.Signup;
import com.sdsmdg.bookshareapp.BSA.api.models.LocalUsers.UserInfo;
import com.sdsmdg.bookshareapp.BSA.api.models.VerifyToken.Detail;
import com.sdsmdg.bookshareapp.BSA.ui.College;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface UsersAPI {
    @FormUrlEncoded
    @POST("users/reg/?format=json")
    Call<Signup> getUserInfo(
            @Field("email") String email,
            @Field("hostel") String hostel,
            @Field("room_no") String roomNo,
            @Field("enr_no") String enrNo,
            @Field("first_name") String firstName,
            @Field("last_name") String lastName,
            @Field("contact_no") String contactNo,
            @Field("fcm_id") String fcm_id,
            @Field("password") String password,
            @Query("college_name") String college
    );

    @FormUrlEncoded
    @POST("login/")
    Call<Login> getToken(
            @Field("email") String email,
            @Field("password") String password
    );

    @GET("books/?format=json")
    Call<List<Book>> getBooksList(
            @Header("Authorization") String token
    );

    @GET("local-books/")
    Call<BookList> getLocalBList(
            @Query("page") String page,
            @Header("Authorization") String token
   );

    @GET("books/?format=json")
    Call<BookList> getBList(
            @Query("page") String page,
            @Header("Authorization") String token
    );

    @GET("guest/?format=json")
    Call<BookList> getGuestBList(
            @Query("page") String page
    );

    @FormUrlEncoded
    @POST("college/")
    Call<College> addCollege(
                @Field("name") String college,
                @Field("domain") String domain
    );

    @FormUrlEncoded
    @POST("books/?format=json")
    Call<Book> addBook(
            @Field("email") String email,
            @Field("title") String title,
            @Field("author") String author,
            @Field("gr_id") String gr_id,
            @Field("ratings_count") Long ratingsCount,
            @Field("rating") Float rating,
            @Field("gr_img_url") String gr_img_url,
            @Field("description") String description,
            @Header("Authorization") String token
    );

    @GET("book/{id}/?format=json")
    Call<BookDetailWithCancel> getBookDetails(
            @Path("id") String id,
            @Query("book_id") String id_same,
            @Header("Authorization") String token
    );

    @GET("user/{id}/?format=json")
    Call<UserDetailWithCancel> getUserDetails(
            @Path("id") String id,
            @Query("user_id") String id_same,
            @Header("Authorization") String token
    );

    @POST("token/")
    Call<Detail> getUserEmail(@Header("Authorization") String token);

    @GET("notifications/")
    Call<Notification_Model> getNotifs(
            @Query("page") String count,
            @Header("Authorization") String token
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
            @Field("message") String message,
            @Header("Authorization") String token
    );

    @FormUrlEncoded
    @POST("notifications/")
    Call<Notifications> acceptNotif(
            @Field("notif_id") String notifId,
            @Field("sender_id") String senderId,
            @Field("sender_name") String senderName,
            @Field("book_id") String bookId,
            @Field("book_title") String bookTitle,
            @Field("process") String process,
            @Field("target_id") String targetId,
            @Field("message") String message,
            @Header("Authorization") String token
    );

    @FormUrlEncoded
    @POST("notifications/")
    Call<Notifications> rejectNotif(
            @Field("notif_id") String notifId,
            @Field("process") String process,
            @Field("message") String message,
            @Header("Authorization") String token
    );

    @FormUrlEncoded
    @POST("notifications/")
    Call<Notifications> cancelNotif(
            @Field("book_id") String bookId,
            @Field("receiver_id") String targetId,
            @Field("process") String process,
            @Header("Authorization") String token
    );

    @PUT("user/{id}/")
    Call<UserInfo> editUserDetails(
            @Path("id") String id,
            @Body UserInfo userInfo
    );


    @FormUrlEncoded
    @POST("password/change/")
    Call<Detail> changePassword(
            @Field("user_id") String id,
            @Field("token") String token,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("fcmid/change/")
    Call<Detail> update_fcm_id(
            @Header("Authorization") String token,
            @Field("fcm_id") String fcm_id
    );


    @PUT("books/")
    Call<Detail> removeBook(
            @Body RemoveBook removeBook,
            @Header("Authorization") String token
    );

    @GET("search/")
    Call<List<Book>> search(
            @Query("search") String searchQuery,
            @Header("Authorization") String token
    );

    @GET("search-user/")
    Call<List<UserInfo>> searchUser(
            @Query("search") String searchQuery,
            @Header("Authorization") String token
    );

    @GET("search-college/")
    Call<List<College>> searchCollege(
            @Query("search") String college
    );

    @Multipart
    @POST("/image/{id}/")
    Call<Signup> uploadImage(@Part MultipartBody.Part file,
                             @Path("id") String id);

    @FormUrlEncoded
    @POST("/me/password/reset/")
    Call<ResponseBody> sendMail(
            @Field("email") String email
    );

    @FormUrlEncoded
    @POST("/me/password/reset/confirm/")
    Call<ResponseBody> confirmPassword(
            @Field("uidb64") String uidb64,
            @Field("token") String token,
            @Field("new_password") String newPassword,
            @Field("confirm_password") String confirmPassword
    );
}
