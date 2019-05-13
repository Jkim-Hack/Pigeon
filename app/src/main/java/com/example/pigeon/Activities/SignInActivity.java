package com.example.pigeon.Activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.pigeon.FirebaseManagers.Accounts.LoggingInHelper;
import com.example.pigeon.FirebaseManagers.FirebaseHelper;
import com.example.pigeon.R;

public class SignInActivity extends AppCompatActivity {

    private EditText emailEnter;
    private EditText passwordEnter;
    private Button signInConfirm;
    private Button signUpNow;

    private AppCompatActivity appCompatActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        FirebaseHelper.build();

        emailEnter = (EditText) (findViewById(R.id.emailEnter));
        passwordEnter = (EditText) (findViewById(R.id.passwordEnter));
        signInConfirm = (Button) (findViewById(R.id.signInConfirm));
        signUpNow = (Button) (findViewById(R.id.signUpNow));

        signInConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivity();
                LoggingInHelper.signInUser(emailEnter.getText().toString(), passwordEnter.getText().toString(), activity, appCompatActivity);
            }
        });

        signUpNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent e = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(e);
            }
        });
    }

    private Activity getActivity(){
        return this;
    }



}
