package com.example.pigeon.Activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.example.pigeon.FirebaseManagers.Accounts.LoggingInHelper;
import com.example.pigeon.FirebaseManagers.FirebaseHelper;
import com.example.pigeon.R;

public class SignUpActivity extends AppCompatActivity {
    private EditText emailSignUp;
    private EditText passwordSignUp;
    private EditText confirmPassword;
    private EditText name;
    private Button signUpButton;
    private Button alreadyAccount;
    AppCompatActivity appCompatActivity = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailSignUp = (EditText) (findViewById(R.id.emailSignUp));
        passwordSignUp = (EditText) (findViewById(R.id.passwordSignUp));
        confirmPassword = (EditText) (findViewById(R.id.confirmPassword2));
        name = (EditText) (findViewById(R.id.name));
        signUpButton = (Button) (findViewById(R.id.signUpButton));
        alreadyAccount = (Button) (findViewById(R.id.alreadyAccount));

        FirebaseHelper.build();

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checks = true; //Has one boolean so that the sign up call is easily accessed.
                if(emailSignUp.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Please enter an Email Address", Toast.LENGTH_SHORT).show();
                    checks = false;
                }
                if(passwordSignUp.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Please enter a Password", Toast.LENGTH_SHORT).show();
                    checks = false;
                }
                if(confirmPassword.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Please Confirm your Password", Toast.LENGTH_SHORT).show();
                    checks = false;
                }
                if(!(passwordSignUp.getText().toString().equals(confirmPassword.getText().toString())))
                {
                    Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                    checks = false;
                }

                if(name.getText().toString().equals("")){
                    checks = false;
                }

                //Final check
                if(checks){
                    Activity activity = getActivity();
                    LoggingInHelper.signUpUser(emailSignUp.getText().toString(), passwordSignUp.getText().toString(), name.getText().toString(), activity, appCompatActivity);
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

    private Activity getActivity(){
        return this;
    }


}
