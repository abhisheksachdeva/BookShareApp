package com.sdsmdg.bookshareapp.BSA.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.api.NetworkingFactory;
import com.sdsmdg.bookshareapp.BSA.api.UsersAPI;
import com.sdsmdg.bookshareapp.BSA.api.models.LocalUsers.UserInfo;
import com.sdsmdg.bookshareapp.BSA.api.models.VerifyToken.Detail;
import com.sdsmdg.bookshareapp.BSA.utils.Helper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    EditText firstName, lastName, contactNo, roomNo;
    Spinner hostelSpinner;
    String id, email;
    SharedPreferences preferences, hostelPref;
    String hostel;
    UserInfo userInfo;
    CustomProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        firstName = (EditText) findViewById(R.id.first_name);
        lastName = (EditText) findViewById(R.id.last_name);
        contactNo = (EditText) findViewById(R.id.contact_no);
        roomNo = (EditText) findViewById(R.id.room_no);
        hostelSpinner = (Spinner) findViewById(R.id.hostel_spinner);

        preferences = getSharedPreferences("Token", MODE_PRIVATE);

        hostelPref = getSharedPreferences("hostel_res_id", MODE_PRIVATE);
        int hostelResId = hostelPref.getInt("hostel_id", R.array.iitr_hostel_list);

        id = preferences.getString("id", null);
        email = preferences.getString("email", null);
        firstName.setText(preferences.getString("first_name", null));
        lastName.setText(preferences.getString("last_name", null));
        contactNo.setText(preferences.getString("contact_no", null));
        roomNo.setText(preferences.getString("room_no", null));
        hostel = preferences.getString("hostel", null);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, hostelResId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hostelSpinner.setAdapter(adapter);
        if (!hostel.equals(null)) {
            int spinnerPosition = adapter.getPosition(hostel);
            hostelSpinner.setSelection(spinnerPosition);
        }

        hostelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hostel = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void saveClicked() {

        progressDialog = new CustomProgressDialog(EditProfileActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.show();

        userInfo = new UserInfo();
        userInfo.setFirstName(firstName.getText().toString());
        userInfo.setLastName(lastName.getText().toString());
        userInfo.setRoomNo(roomNo.getText().toString());
        userInfo.setContactNo(contactNo.getText().toString());
        userInfo.setHostel(hostel);

        UsersAPI usersAPI = NetworkingFactory.getLocalInstance().getUsersAPI();
        Call<Detail> call = usersAPI.editUserDetails(
                "Token " + preferences.getString("token", null),
                preferences.getString("id", null),
                userInfo
        );
        call.enqueue(new Callback<Detail>() {
            @Override
            public void onResponse(Call<Detail> call, Response<Detail> response) {
                progressDialog.dismiss();
                if (response.body() != null) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("first_name", firstName.getText().toString());
                    editor.putString("last_name", lastName.getText().toString());
                    editor.putString("room_no", roomNo.getText().toString());
                    editor.putString("contact_no", contactNo.getText().toString());
                    editor.putString("hostel", hostel);
                    editor.apply();
                    Helper.setUserName(firstName.getText().toString() + " " + lastName.getText().toString());
                    if (response.body().getDetail().equals("A verification code has been sent")){
                        editor.clear();
                        editor.apply();
                        Intent verifyOtpIntent = new Intent
                                (EditProfileActivity.this, VerifyOtpActivity.class);
                        verifyOtpIntent.setFlags
                                (Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        verifyOtpIntent.putExtra("email", email);
                        startActivity(verifyOtpIntent);
                        finish();
                    }else if (response.body().getDetail().equals("The profile has been updated")){
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 1000);
                    }
                } else {
                    Toast.makeText(EditProfileActivity.this, R.string.connection_failed, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Detail> call, Throwable t) {
                t.printStackTrace();
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), R.string.connection_failed, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save_details){
            saveClicked();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, MyProfile.class);
        i.putExtra("id", preferences.getString("id", preferences.getString("id", "")));
        startActivity(i);

    }
}
