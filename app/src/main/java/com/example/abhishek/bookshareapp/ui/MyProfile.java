package com.example.abhishek.bookshareapp.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import com.example.abhishek.bookshareapp.utils.FileUtils;
import com.example.abhishek.bookshareapp.utils.Helper;
import com.example.abhishek.bookshareapp.utils.PermissionUtils;
import com.klinker.android.sliding.SlidingActivity;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import id.zelory.compressor.Compressor;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyProfile extends SlidingActivity {
    TextView userName, userEmail, address;
    UserInfo user;
    String id;
    String url = CommonUtilities.local_books_api_url+"image/"+Helper.getUserId()+"/";
    int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    String userChoosenTask;

    @Override
    public void init(Bundle savedInstanceState) {
        setTitle("My Profile");
        setPrimaryColors(
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorPrimaryDark)
        );
        setContent(R.layout.activity_my_profile);

        String url = CommonUtilities.local_books_api_url+"image/"+Helper.getUserId()+"/";
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
                selectImage(v);
            }
            });

        userName = (TextView) findViewById(R.id.username);
        userEmail = (TextView) findViewById(R.id.useremail);
        address = (TextView) findViewById(R.id.address);

        id = getIntent().getExtras().getString("id");
        getUserInfoDetails(id);
    }

    public void  selectImage(View view){
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(MyProfile.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = PermissionUtils.checkPermission(MyProfile.this);
                if (items[item].equals("Take Photo")) {
                    userChoosenTask="Take Photo";
                    if(result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask="Choose from Library";
                    if(result)
                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }
    private void galleryIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PermissionUtils.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        if (resCode == Activity.RESULT_OK && data != null) {
            if (reqCode == SELECT_FILE) {
            onGalleryImageResult(data);
        }
            else if(reqCode == REQUEST_CAMERA){
                onCaptureImageResult(data);
            }
    }
    }

    private void onCaptureImageResult(Intent data) {

        try {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File file = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
            file.createNewFile();
            fo = new FileOutputStream(file);
            fo.write(bytes.toByteArray());
            fo.close();
            setImage(thumbnail);
            sendToServer(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this,e.toString(), Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        } catch (NullPointerException e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void onGalleryImageResult(Intent data){
        Uri uri = data.getData();
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        setImage(bitmap);
        File file = getFile(uri, bitmap);
        if (file != null) {
            sendToServer(file);
        } else {
            Toast.makeText(this, "Can't upload image", Toast.LENGTH_SHORT).show();
        }
    }

    public File getFile(Uri uri, Bitmap bitmap){
        File file = null;
        String path = FileUtils.getPath(this,uri);
        if(path==null){
            try {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                file = new File(this.getCacheDir(),
                        System.currentTimeMillis() + ".jpg");
                FileOutputStream fo;
                file.createNewFile();
                fo = new FileOutputStream(file);
                fo.write(bytes.toByteArray());
                fo.close();
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
                file = null;
            }
            catch (IOException e) {
                e.printStackTrace();
                file = null;
            }
            catch (java.lang.NullPointerException e){
                e.printStackTrace();
                file = null;
            }
        }
        else {
            try {
                file = new File(path);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        return file;
    }

    public void sendToServer(File file){

        UsersAPI api = NetworkingFactory.getLocalInstance().getUsersAPI();
        try {
                File compressedFile = new Compressor.Builder(this)
                        .setMaxWidth(640)
                        .setMaxHeight(480)
                        .setQuality(75)
                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                        .build()
                        .compressToFile(file);

                RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), compressedFile);
                MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestBody);
                Call<Signup> call = api.uploadImage(body, Helper.getUserId());
            Toast.makeText(getApplicationContext(), "Updating picture. Please wait.", Toast.LENGTH_SHORT).show();
            call.enqueue(new Callback<Signup>() {
                    @Override
                    public void onResponse(Call<Signup> call, Response<Signup> response) {
                        if (response.body() != null) {
                            String detail = response.body().getDetail();
                            Toast.makeText(getApplicationContext(), detail, Toast.LENGTH_SHORT).show();
                            Helper.imageChanged = true;
                            Picasso.with(getApplicationContext())
                                    .load(url)
                                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                                    .into(new Target() {
                                        @Override
                                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                            setImage(bitmap);
                                        }

                                        @Override
                                        public void onBitmapFailed(Drawable errorDrawable) {
                                            Toast.makeText(getApplicationContext(), "failed to load image", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onFailure(Call<Signup> call, Throwable t) {
                        Log.d("BookDetails fail", t.toString());
                    }
                });
        }
        catch (NullPointerException e){
            Toast.makeText(this,e.toString(), Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


}
