package com.example.abhishek.bookshareapp.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.abhishek.bookshareapp.R;
import com.example.abhishek.bookshareapp.api.NetworkingFactory;
import com.example.abhishek.bookshareapp.api.UsersAPI;
import com.example.abhishek.bookshareapp.api.models.UserInfo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    EditText firstName, lastName, contactNo, email, roomNo, hostel, college;
    Button saveButton;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        firstName = (EditText) findViewById(R.id.first_name);
        lastName = (EditText) findViewById(R.id.last_name);
        email = (EditText) findViewById(R.id.email);
        contactNo = (EditText) findViewById(R.id.contact_no);
        roomNo = (EditText) findViewById(R.id.room_no);
        hostel = (EditText) findViewById(R.id.hostel);
        college = (EditText) findViewById(R.id.college);

        preferences = getSharedPreferences("Token", MODE_PRIVATE);
        firstName.setText(preferences.getString("first_name", null));
        lastName.setText(preferences.getString("last_name", null));
        email.setText(preferences.getString("email", null));
        contactNo.setText(preferences.getString("contact_no", null));
        roomNo.setText(preferences.getString("room_no", null));
        hostel.setText(preferences.getString("hostel", null));
        college.setText(preferences.getString("college", null));

    }

    public void saveClicked(View view) {

        UserInfo userInfo = new UserInfo();
        userInfo.setFirstName(firstName.getText().toString());
        userInfo.setLastName(lastName.getText().toString());
        userInfo.setEmail(email.getText().toString());
        userInfo.setRoomNo(roomNo.getText().toString());
        userInfo.setCollege(college.getText().toString());
        userInfo.setContactNo(contactNo.getText().toString());
        userInfo.setHostel(hostel.getText().toString());

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
                    editor.putString("email", email.getText().toString());
                    editor.putString("room_no", roomNo.getText().toString());
                    editor.putString("college", college.getText().toString());
                    editor.putString("contact_no", contactNo.getText().toString());
                    editor.putString("hostel", hostel.getText().toString());

                    editor.apply();

                } else {
                    Log.i("harshit", "response.body() is null)");
                }
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), "Check your network connectivity and try again", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
