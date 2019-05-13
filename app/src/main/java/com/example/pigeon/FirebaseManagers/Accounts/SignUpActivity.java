package com.example.pigeon.FirebaseManagers.Accounts;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pigeon.R;

public class SignUpActivity extends AppCompatActivity {
    EditText emailSignUp = (EditText) (findViewById(R.id.emailSignUp));
    EditText passwordSignUp = (EditText) (findViewById(R.id.passwordSignUp));
    EditText confirmPassword = (EditText) (findViewById(R.id.confirmPassword));
    Button signUpButton = (Button) (findViewById(R.id.signUpButton));
    Button alreadyAccount = (Button) (findViewById(R.id.alreadyAccount));
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                while(emailSignUp.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Please enter an Email Address", Toast.LENGTH_SHORT).show();
                }
                while(passwordSignUp.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Please enter a Password", Toast.LENGTH_SHORT).show();
                }
                while(confirmPassword.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Please Confirm your Password", Toast.LENGTH_SHORT).show();
                }
                while(!(passwordSignUp.getText().toString().equals(confirmPassword.getText().toString())))
                {
                    Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
            }
        });

        alreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(i);
            }
        });

    }
}
