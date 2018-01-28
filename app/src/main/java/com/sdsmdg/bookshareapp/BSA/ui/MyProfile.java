
package com.sdsmdg.bookshareapp.BSA.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.api.NetworkingFactory;
import com.sdsmdg.bookshareapp.BSA.api.UsersAPI;
import com.sdsmdg.bookshareapp.BSA.api.models.LocalBooks.Book;
import com.sdsmdg.bookshareapp.BSA.api.models.LocalUsers.UserDetailWithCancel;
import com.sdsmdg.bookshareapp.BSA.api.models.LocalUsers.UserInfo;
import com.sdsmdg.bookshareapp.BSA.api.models.Signup;
import com.sdsmdg.bookshareapp.BSA.api.models.UserImageResult;
import com.sdsmdg.bookshareapp.BSA.ui.adapter.Local.BookAdapter;
import com.sdsmdg.bookshareapp.BSA.utils.CommonUtilities;
import com.sdsmdg.bookshareapp.BSA.utils.FileUtils;
import com.sdsmdg.bookshareapp.BSA.utils.Helper;
import com.sdsmdg.bookshareapp.BSA.utils.PermissionUtils;
import com.sdsmdg.bookshareapp.BSA.utils.SPDataLoader;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.OkHttpDownloader;
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
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyProfile extends AppCompatActivity {

    final String TAG = MyProfile.class.getSimpleName();
    private static final int SEARCH_RESULTS_REQUEST_CODE = 1001;

    TextView userName, userEmail, address, titleBooksCount;
    UserInfo user;
    String id;
    String url = CommonUtilities.local_books_api_url + "image/" + Helper.getUserId() + "/";
    String imageUrl;
    int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    String userChoosenTask;
    CircleImageView profilePicture;
    ImageView backgroundImageView;
    NestedScrollView scrollView;
    SPDataLoader loader = new SPDataLoader();
    SharedPreferences prefs;
    List<Book> booksList;
    BookAdapter adapter;
    RecyclerView mRecyclerView;
    String Resp;
    FloatingActionButton button;
    TextView noItemsTextView;
    ProgressBar bookProgressBar;

    private int noOfBooks = 0;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("My Profile");
        setContentView(R.layout.activity_my_profile);

        prefs = getApplicationContext().getSharedPreferences("Token", Context.MODE_PRIVATE);

        bookProgressBar = (ProgressBar) findViewById(R.id.book_progress_bar);
        noItemsTextView = (TextView) findViewById(R.id.no_items_text);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        titleBooksCount = (TextView) findViewById(R.id.title_books_count);

        SharedPreferences preferences = getSharedPreferences("Token", MODE_PRIVATE);
        token = preferences.getString("token", null);
        id = preferences.getString("id", "");

        setUpRecyclerView(id);

        getUserInfoDetails(id);
        getProfilePicture(null);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        profilePicture = (CircleImageView) findViewById(R.id.profile_picture);
        userName = (TextView) findViewById(R.id.user_name);
        userEmail = (TextView) findViewById(R.id.user_email);
        address = (TextView) findViewById(R.id.address);
        backgroundImageView = (ImageView) findViewById(R.id.background_image);
        scrollView = (NestedScrollView) findViewById(R.id.scroll);
        scrollView.setSmoothScrollingEnabled(true);
        mRecyclerView.setNestedScrollingEnabled(false);

        button = (FloatingActionButton) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MyProfile.this, SearchResultsActivity.class);
                startActivityForResult(i, SEARCH_RESULTS_REQUEST_CODE);
            }
        });
    }


    //This function is called from BookAdapter, when a book is sucessfully removed
    public void onBookRemoved() {
        noOfBooks -= 1;
        Log.i(TAG, "onBookRemoved: 1");
        titleBooksCount.setText("Books" + "(" + String.valueOf(noOfBooks) + ")");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_book, menu);
        return true;
    }

    public void getUserInfoDetails(String id) {
        bookProgressBar.setVisibility(View.VISIBLE);
        UsersAPI api = NetworkingFactory.getLocalInstance().getUsersAPI();
        Call<UserDetailWithCancel> call = api.getUserDetails(id, id, "Token " + prefs
                .getString("token", null));
        call.enqueue(new Callback<UserDetailWithCancel>() {
            @Override
            public void onResponse(@NonNull Call<UserDetailWithCancel> call, @NonNull Response<UserDetailWithCancel> response) {
                bookProgressBar.setVisibility(View.GONE);
                if (response.body() != null) {
                    Resp = response.toString();
                    user = response.body().getUserInfo();
                    List<Book> booksTempInfoList = user.getUserBookList();
                    if (booksTempInfoList.size() == 0) {
                        noItemsTextView.setVisibility(View.VISIBLE);
                    }
                    booksList.clear();
                    booksList.addAll(booksTempInfoList);
                    noOfBooks = booksList.size();
                    titleBooksCount.setText("Books" + "(" + noOfBooks + ")");
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserDetailWithCancel> call, @NonNull Throwable t) {
                bookProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void getProfilePicture(final String detail) {
        new Picasso.Builder(MyProfile.this).
                downloader(new OkHttp3Downloader(getOkHttpClient())).build()
                .load(CommonUtilities.currentUserImageUrl)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        if (detail != null) {
                            Toast.makeText(getApplicationContext(), detail, Toast.LENGTH_SHORT).show();
                        }
                        profilePicture.setImageBitmap(bitmap);
                        backgroundImageView.setImageBitmap(bitmap);
                        Blurry.with(getApplicationContext())
                                .radius(40)
                                .sampling(1)
                                .color(Color.argb(66, 0, 0, 0))
                                .async()
                                .capture(findViewById(R.id.background_image))
                                .into((ImageView) findViewById(R.id.background_image));
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        profilePicture.setImageResource(R.drawable.user_default_image);
                        backgroundImageView.setImageResource(R.drawable.user_default_image);
                        Blurry.with(getApplicationContext())
                                .radius(40)
                                .sampling(1)
                                .color(Color.argb(66, 0, 0, 0))
                                .async()
                                .capture(findViewById(R.id.background_image))
                                .into((ImageView) findViewById(R.id.background_image));
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                    }
                });
    }

    private OkHttpClient getOkHttpClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(@NonNull Chain chain) throws IOException {
                        Request newRequest = chain.request().newBuilder()
                                .addHeader("Authorization", "Token " + prefs
                                        .getString("token", null))
                                .build();
                        return chain.proceed(newRequest);
                    }
                }).build();
        return client;
    }

    private void setUpRecyclerView(String id) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MyProfile.this));
        booksList = new ArrayList<>();
        adapter = new BookAdapter(this, id, booksList, this);
        mRecyclerView.setAdapter(adapter);
        adapter.setUpItemTouchHelper(mRecyclerView);
        adapter.setUpAnimationDecoratorHelper(mRecyclerView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        userName.setText(loader.getUserName(this));
        userEmail.setText(loader.getUserEmail(this));
        address.setText(loader.getRoomNo(this) + ", " + loader.getHostel(this));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return (true);
        }
        return (super.onOptionsItemSelected(item));
    }

    public void editProfileClicked(View view) {
        Intent i = new Intent(this, EditProfileActivity.class);
        startActivity(i);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void changeImageClicked(View view) {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MyProfile.this);
        builder.setTitle("Add Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (PermissionUtils.checkCameraPermission(MyProfile.this)) {
                        cameraIntent();
                    }
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (PermissionUtils.checkStoragePermission(MyProfile.this))
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
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionUtils.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if (userChoosenTask.equals("Choose from Library"))
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
            } else if (reqCode == REQUEST_CAMERA) {
                onCaptureImageResult(data);
            }
        } else if (reqCode == SEARCH_RESULTS_REQUEST_CODE){
            getUserInfoDetails(id);
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private void onCaptureImageResult(Intent data) {
//        Sentry.getContext().recordBreadcrumb(
//                new BreadcrumbBuilder().setMessage("Inside onCaptureImageResult").build()
//        );
//
//        // Set the user in the current context.
//        Sentry.getContext().setUser(
//                new UserBuilder().setEmail(Helper.getUserEmail()).build()
//        );


        if (isExternalStorageWritable()) {
            try {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        System.currentTimeMillis() + ".jpg");
                FileOutputStream fo;
                fo = new FileOutputStream(file);
                fo.write(bytes.toByteArray());
                fo.close();
                sendToServer(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, R.string.file_not_found, Toast.LENGTH_SHORT).show();
//                Sentry.capture(e);

            } catch (IOException e) {
                Toast.makeText(this, "Unable to read the file", Toast.LENGTH_SHORT).show();
//                Sentry.capture(e);
                e.printStackTrace();
            } catch (NullPointerException e) {
                Toast.makeText(this, R.string.file_not_found, Toast.LENGTH_SHORT).show();
//                Sentry.capture(e);
                e.printStackTrace();
            } catch (Exception e) {
                Toast.makeText(this, "Unable to read the file", Toast.LENGTH_SHORT).show();
                //Sentry.capture(e);
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this, "No extrenal storage available", Toast.LENGTH_SHORT).show();
        }
    }

    private void onGalleryImageResult(Intent data) {
        Uri uri = data.getData();
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e){
            Toast.makeText(this, "Image is too big to load!!", Toast.LENGTH_SHORT).show();
        }
        File file = getFile(uri, bitmap);
        if (file != null) {
            sendToServer(file);
        } else {
            Toast.makeText(this, "Unable to upload image", Toast.LENGTH_SHORT).show();
        }
    }

    public File getFile(Uri uri, Bitmap bitmap) {
//        Sentry.getContext().recordBreadcrumb(
//                new BreadcrumbBuilder().setMessage("Inside getFile").build()
//        );
//
//        // Set the user in the current context.
//        Sentry.getContext().setUser(
//                new UserBuilder().setEmail(Helper.getUserEmail()).build()
//        );

        File file = null;
        String path = FileUtils.getPath(this, uri);
        if (path == null) {
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
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
                file = null;
            }
        } else {
            try {
                file = new File(path);
            } catch (Exception e) {
                e.printStackTrace();
//                Sentry.capture(e);
            }
        }
        return file;
    }

    public void sendToServer(File file) {
//        Sentry.getContext().recordBreadcrumb(
//                new BreadcrumbBuilder().setMessage("Inside MyProfile/sendToServer").build()
//        );
//
//        // Set the user in the current context.
//        Sentry.getContext().setUser(
//                new UserBuilder().setEmail(Helper.getUserEmail()).build()
//        );


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
            Call<UserImageResult> call = api.uploadImage("Token " + token, body, Helper.getUserId());
            Toast.makeText(getApplicationContext(), "Updating picture. Please wait.", Toast.LENGTH_SHORT).show();
            call.enqueue(new Callback<UserImageResult>() {
                @Override
                public void onResponse(@NonNull Call<UserImageResult> call, @NonNull Response<UserImageResult> response) {
                    if (response.body() != null) {
                        final String detail = response.body().getDetail();
                        if (detail.equals("Profile picture changed")) {
                            Helper.imageChanged = true;
                            getProfilePicture(detail);
                        }else{
                            Toast.makeText(MyProfile.this, detail, Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<UserImageResult> call, @NonNull Throwable t) {
                    Toast.makeText(getApplicationContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (NullPointerException e) {
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
//            Sentry.capture(e);
        } catch (OutOfMemoryError e){
            Toast.makeText(this, "Image is too big to upload!!", Toast.LENGTH_SHORT).show();
        }
    }

    public void editBooksClicked(View view) {
        Intent i = new Intent(this, MyProfile.class);
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
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}