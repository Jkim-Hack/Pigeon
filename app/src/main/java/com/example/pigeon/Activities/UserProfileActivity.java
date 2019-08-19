package com.example.pigeon.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pigeon.R;
import com.example.pigeon.common.UserInfo.ContactInfo;

public class UserProfileActivity extends AppCompatActivity {

    private ContactInfo contactInfo;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        TextView nameField = findViewById(R.id.nameField);
        TextView emailField = findViewById(R.id.emailField);
        ImageView image = findViewById(R.id.profile_image);

        nameField.setText(contactInfo.getName());

        super.onCreate(savedInstanceState);
    }

    public void setContactInfo(ContactInfo contactInfo) {
        this.contactInfo = contactInfo;
    }
}
