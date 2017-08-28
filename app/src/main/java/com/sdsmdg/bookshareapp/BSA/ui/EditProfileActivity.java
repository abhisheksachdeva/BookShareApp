package com.sdsmdg.bookshareapp.BSA.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.sdsmdg.bookshareapp.BSA.utils.Helper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    EditText firstName, lastName, contactNo, roomNo;
    Spinner hostelSpinner;
    String id;
    SharedPreferences preferences, hostelPref;
    String hostel;
    UserInfo userInfo;

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

    public void saveClicked(View view) {

        userInfo = new UserInfo();
        userInfo.setFirstName(firstName.getText().toString());
        userInfo.setLastName(lastName.getText().toString());
        userInfo.setRoomNo(roomNo.getText().toString());
        userInfo.setContactNo(contactNo.getText().toString());
        userInfo.setHostel(hostel);

        UsersAPI usersAPI = NetworkingFactory.getLocalInstance().getUsersAPI();
        Call<UserInfo> call = usersAPI.editUserDetails(
                preferences.getString("id", null),
                userInfo
        );
        call.enqueue(new Callback<UserInfo>() {
            @Override

            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                if (response.body() != null) {
                    Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("first_name", firstName.getText().toString());
                    editor.putString("last_name", lastName.getText().toString());
                    editor.putString("room_no", roomNo.getText().toString());
                    editor.putString("contact_no", contactNo.getText().toString());
                    editor.putString("hostel", hostel);
                    editor.apply();
                    Helper.setUserName(firstName.getText().toString() + " " + lastName.getText().toString());

                } else {
                    Toast.makeText(EditProfileActivity.this, R.string.connection_failed, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), R.string.connection_failed, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, MyProfile.class);
        i.putExtra("id", preferences.getString("id", preferences.getString("id", "")));
        startActivity(i);

    }
}
