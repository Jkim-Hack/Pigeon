package com.example.pigeon.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.pigeon.common.UserInfo.User;
import com.example.pigeon.FirebaseManagers.FirebaseHelper;
import com.example.pigeon.R;

public class MainActivity extends AppCompatActivity {
    public static User user; //The current user. Subject to change where this global variable should be placed
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseHelper.build();

    }


}
