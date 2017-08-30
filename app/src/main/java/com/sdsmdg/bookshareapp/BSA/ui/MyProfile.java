
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
import android.widget.TextView;
import android.widget.Toast;

import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.api.NetworkingFactory;
import com.sdsmdg.bookshareapp.BSA.api.UsersAPI;
import com.sdsmdg.bookshareapp.BSA.api.models.LocalBooks.Book;
import com.sdsmdg.bookshareapp.BSA.api.models.LocalUsers.UserDetailWithCancel;
import com.sdsmdg.bookshareapp.BSA.api.models.LocalUsers.UserInfo;
import com.sdsmdg.bookshareapp.BSA.api.models.Signup;
import com.sdsmdg.bookshareapp.BSA.ui.adapter.Local.BookAdapter;
import com.sdsmdg.bookshareapp.BSA.utils.CommonUtilities;
import com.sdsmdg.bookshareapp.BSA.utils.FileUtils;
import com.sdsmdg.bookshareapp.BSA.utils.Helper;
import com.sdsmdg.bookshareapp.BSA.utils.PermissionUtils;
import com.sdsmdg.bookshareapp.BSA.utils.SPDataLoader;
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

    TextView userName, userEmail, address, titleBooksCount;
    UserInfo user;
    String id;
    String url = CommonUtilities.local_books_api_url + "image/" + Helper.getUserId() + "/";
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
    CustomProgressDialog customProgressDialog;
    FloatingActionButton button;
    TextView noItemsTextView;

    private int noOfBooks = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("My Profile");
        setContentView(R.layout.activity_my_profile);

        customProgressDialog = new CustomProgressDialog(MyProfile.this);
        customProgressDialog.setCancelable(false);
        customProgressDialog.show();
        prefs = getApplicationContext().getSharedPreferences("Token", Context.MODE_PRIVATE);

        noItemsTextView = (TextView) findViewById(R.id.no_items_text);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        titleBooksCount = (TextView) findViewById(R.id.title_books_count);

        SharedPreferences preferences = getSharedPreferences("Token", MODE_PRIVATE);
        String id = preferences.getString("id", "");

        setUpRecyclerView(id);

        getUserInfoDetails(id);

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
                startActivity(i);
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
        UsersAPI api = NetworkingFactory.getLocalInstance().getUsersAPI();
        Call<UserDetailWithCancel> call = api.getUserDetails(id, id, "Token " + prefs
                .getString("token", null));
        call.enqueue(new Callback<UserDetailWithCancel>() {
            @Override
            public void onResponse(Call<UserDetailWithCancel> call, Response<UserDetailWithCancel> response) {
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


                    Picasso.with(getApplicationContext()).load(url).into(profilePicture);

                    Picasso.with(getApplicationContext()).load(url).into(backgroundImageView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
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
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        customProgressDialog.dismiss();
                    }
                }, 1000);

            }

            @Override
            public void onFailure(Call<UserDetailWithCancel> call, Throwable t) {
                customProgressDialog.dismiss();
            }
        });
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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
        }
    }

    private void onCaptureImageResult(Intent data) {

        try {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("toReadName");
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
            Toast.makeText(this, R.string.file_not_found, Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Toast.makeText(this, "Unable to read the file", Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        } catch (NullPointerException e) {
            Toast.makeText(this, R.string.file_not_found, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (Exception e) {
            Toast.makeText(this, "Unable to read the file", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void onGalleryImageResult(Intent data) {
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
            Toast.makeText(this, "Unable to upload image", Toast.LENGTH_SHORT).show();
        }
    }

    public File getFile(Uri uri, Bitmap bitmap) {
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
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                file = null;
            } catch (IOException e) {
                e.printStackTrace();
                file = null;
            } catch (java.lang.NullPointerException e) {
                e.printStackTrace();
                file = null;
            }
        } else {
            try {
                file = new File(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public void sendToServer(File file) {

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
                                        Toast.makeText(getApplicationContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                                    }
                                });
                    }
                }

                @Override
                public void onFailure(Call<Signup> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Faile to load image", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (NullPointerException e) {
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
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
    protected void onRestart() {
        super.onRestart();
    }


}