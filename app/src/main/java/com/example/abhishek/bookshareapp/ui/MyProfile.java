package com.example.abhishek.bookshareapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhishek.bookshareapp.R;
import com.example.abhishek.bookshareapp.api.NetworkingFactory;
import com.example.abhishek.bookshareapp.api.UsersAPI;
import com.example.abhishek.bookshareapp.api.models.Signup;
import com.example.abhishek.bookshareapp.api.models.UserInfo;
import com.example.abhishek.bookshareapp.utils.CommonUtilities;
import com.example.abhishek.bookshareapp.utils.Helper;
import com.klinker.android.sliding.SlidingActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URL;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyProfile extends SlidingActivity {
    TextView userName, userEmail, address;
    UserInfo user;
    String id;

    @Override
    public void init(Bundle savedInstanceState) {
        setTitle("My Profile");
        setPrimaryColors(
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorPrimaryDark)
        );
        setContent(R.layout.activity_my_profile);
        String url = "http://192.168.1.2:8000/"+"image/"+Helper.getUserId()+"/";
        Picasso.with(getApplicationContext())
                .load(url)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        setImage(bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        Toast.makeText(getApplicationContext(),"failed to load image", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                    }
                });
        setFab(R.color.BGyellow, R.drawable.plus, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 0);
            }
            });

        userName = (TextView) findViewById(R.id.username);
        userEmail = (TextView) findViewById(R.id.useremail);
        address = (TextView) findViewById(R.id.address);

        id = getIntent().getExtras().getString("id");
        getUserInfoDetails(id);
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        if (resCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            setImage(bitmap);
            sendToServer(uri);
        }
    }

    public void sendToServer(Uri uri){
        OkHttpClient.Builder httpclient = new OkHttpClient.Builder();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.2:8000/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpclient.build())
                .build();
        UsersAPI api = retrofit.create(UsersAPI.class);
//        UsersAPI api = NetworkingFactory.getLocalInstance().getUsersAPI();
        try {
            String[] proj = {MediaStore.Audio.Media.DATA};
            Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            File file = new File(path);
//            File compressedFile = Compressor.getDefault(this).compressToFile(file);
            File compressedFile = new Compressor.Builder(this)
                    .setMaxWidth(640)
                    .setMaxHeight(480)
                    .setQuality(75)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                    .build()
                    .compressToFile(file);

            Toast.makeText(this, String.valueOf(file.length()/1024), Toast.LENGTH_LONG).show();
            Toast.makeText(this, String.valueOf(compressedFile.length()/1024), Toast.LENGTH_LONG).show();
            RequestBody fbody = RequestBody.create(MediaType.parse("image/jpeg"),compressedFile);
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), fbody);
            Call<Signup> call = api.uploadImage(body, Helper.getUserId());
            call.enqueue(new Callback<Signup>() {
                @Override
                public void onResponse(Call<Signup> call, Response<Signup> response) {
                    if (response.body() != null) {
                        String detail = response.body().getDetail();
                        Log.d("Userprofile  Response:", detail);
                        Toast.makeText(getApplicationContext(), detail, Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Signup> call, Throwable t) {
                    Log.d("BookDetails fail", t.toString());
                }
            });
        }
        catch (NullPointerException e){
            Toast.makeText(this,"Can't upload image", Toast.LENGTH_SHORT).show();
        }

    }


    public void getUserInfoDetails(String id) {
        UsersAPI api = NetworkingFactory.getLocalInstance().getUsersAPI();
        Call<UserInfo> call = api.getUserDetails(id);
        call.enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                if (response.body() != null) {
                    Log.d("UserProfile Response:", response.toString());
                    user = response.body();
                    userName.setText(user.getFirstName() + " " + user.getLastName());
                    userEmail.setText(user.getEmail());
                    address.setText(user.getRoomNo() + ", " + user.getHostel());

                }
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                Log.d("BookDetails fail", t.toString());
            }
        });
    }

    public void editProfile(View view) {
        Intent i = new Intent(this, EditProfileActivity.class);
        startActivity(i);
        finish();
    }

    public void myBooks(View view) {
        Intent i = new Intent(this, MyBooks.class);
        startActivity(i);

    }

    public void changePassword(View view) {
        Intent i = new Intent(this, ChangePasswordActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
