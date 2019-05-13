package com.example.pigeon.FirebaseManagers.Accounts;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.pigeon.R;

public class SignInActivity extends AppCompatActivity {

    EditText emailEnter = (EditText) (findViewById(R.id.emailEnter));
    EditText passwordEnter = (EditText) (findViewById(R.id.passwordEnter));
    Button signInConfirm = (Button) (findViewById(R.id.signInConfirm));
    Button signUpNow = (Button) (findViewById(R.id.signUpNow));
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        signUpNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent e = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(e);
            }
        });
    }
}
