package com.example.abhishek.bookshareapp.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhishek.bookshareapp.R;
import com.example.abhishek.bookshareapp.api.NetworkingFactory;
import com.example.abhishek.bookshareapp.api.UsersAPI;
import com.example.abhishek.bookshareapp.api.models.LocalBooks.Book;
import com.example.abhishek.bookshareapp.api.models.Signup;
import com.example.abhishek.bookshareapp.api.models.UserInfo;
import com.example.abhishek.bookshareapp.ui.adapter.Local.BooksAdapterSimple;
import com.example.abhishek.bookshareapp.utils.CommonUtilities;
import com.example.abhishek.bookshareapp.utils.FileUtils;
import com.example.abhishek.bookshareapp.utils.Helper;
import com.example.abhishek.bookshareapp.utils.PermissionUtils;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import jp.wasabeef.blurry.Blurry;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyProfile extends AppCompatActivity {

    final String TAG = MyProfile.class.getSimpleName();

    TextView userName, userEmail, address, booksCount;
    UserInfo user;
    RecyclerView userBooksListView;
    List<Book> userBooksList = new ArrayList<>();
    String id;
    String url = CommonUtilities.local_books_api_url + "image/" + Helper.getUserId() + "/";
    int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    String userChoosenTask;
    CircleImageView profilePicture;
    BooksAdapterSimple adapter;
    ImageView backgroundImageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("My Profile");
        setContentView(R.layout.activity_my_profile);

        profilePicture = (CircleImageView)findViewById(R.id.profile_picture);
        userName = (TextView) findViewById(R.id.user_name);
        userEmail = (TextView)findViewById(R.id.user_email);
        address = (TextView)findViewById(R.id.address);
        userBooksListView = (RecyclerView)findViewById(R.id.user_books_list_view);
        backgroundImageView = (ImageView)findViewById(R.id.background_image);
        booksCount = (TextView)findViewById(R.id.books_count);

        userBooksListView = (RecyclerView)findViewById(R.id.user_books_list_view);
        userBooksListView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BooksAdapterSimple(this, userBooksList, new BooksAdapterSimple.OnItemClickListener() {
            @Override
            public void onItemClick(Book book) {
                Log.i("Click", "onItemClick");
            }
        });
        userBooksListView.setAdapter(adapter);
        userBooksListView.setNestedScrollingEnabled(false);

        String url = CommonUtilities.local_books_api_url+"image/"+Helper.getUserId()+"/";

        id = getIntent().getExtras().getString("id");
        getUserInfoDetails(id);
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
                    userBooksList.clear();
                    userBooksList.addAll(user.getUserBookList());
                    booksCount.setText(String.valueOf(userBooksList.size()));
                    adapter.notifyDataSetChanged();
                    Picasso.with(getApplicationContext()).load(url).into(profilePicture);
                    Picasso.with(getApplicationContext()).load(url).into(backgroundImageView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            Log.i(TAG, "onSuccess: called");
                            Blurry.with(getApplicationContext())
                                    .radius(40)
                                    .sampling(1)
                                    .color(Color.argb(66, 0, 0, 0))
                                    .async()
                                    .capture(findViewById(R.id.background_image))
                                    .into((ImageView) findViewById(R.id.background_image));
                        }

                        @Override
                        public void onError() {

                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                Log.d("BookDetails fail", t.toString());
            }
        });
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
                            final String detail = response.body().getDetail();
                            Helper.imageChanged = true;
                            Picasso.with(getApplicationContext())
                                    .load(url)
                                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                                    .into(new Target() {
                                        @Override
                                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                            Toast.makeText(getApplicationContext(), detail, Toast.LENGTH_SHORT).show();
                                            profilePicture.setImageBitmap(bitmap);
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
