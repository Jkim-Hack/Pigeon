package com.example.pigeon.FirebaseManagers.Accounts;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.pigeon.R;

public class SignInActivity extends AppCompatActivity {

    EditText emailEnter = (EditText) (findViewById(R.id.emailEnter));
    EditText passwordEnter = (EditText) (findViewById(R.id.passwordEnter));
    Button signInConfirm = (Button) (findViewById(R.id.signInConfirm));
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
    }
}
