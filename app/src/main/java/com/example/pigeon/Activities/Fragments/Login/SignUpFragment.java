package com.example.pigeon.Activities.Fragments.Login;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pigeon.FirebaseManagers.Accounts.LoggingInHelper;
import com.example.pigeon.R;

public class SignUpFragment extends Fragment {

    private EditText emailSignUp;
    private EditText nameSignUp;
    private EditText passwordSignUp;
    private Button signUpButton;

    public static SignUpFragment newInstance(int position){
        Bundle args = new Bundle();
        args.putInt("ARG_PAGE", position);
        SignUpFragment signUpFragment = new SignUpFragment();
        signUpFragment.setArguments(args);
        return signUpFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sign_up_content, container, false);

        emailSignUp = view.findViewById(R.id.emailInputSU);
        nameSignUp = view.findViewById(R.id.nameInput);
        passwordSignUp = view.findViewById(R.id.passwordInputSU);
        signUpButton = view.findViewById(R.id.signupbutton);

        final Context appContext = this.getActivity().getApplicationContext();
        final AppCompatActivity appCompatActivity = (AppCompatActivity) this.getActivity(); //Gets the sign in activity
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checks = true; //Has one boolean so that the sign up call is easily accessed.
                if(emailSignUp.getText().toString().equals("")) {
                    Toast.makeText(appContext, "Please enter an email Address", Toast.LENGTH_SHORT).show();
                    checks = false;
                }
                if(passwordSignUp.getText().toString().equals("")) {
                    Toast.makeText(appContext, "Please enter a password", Toast.LENGTH_SHORT).show();
                    checks = false;
                }
                if(nameSignUp.getText().toString().equals("")){
                    Toast.makeText(appContext, "Please enter a name", Toast.LENGTH_SHORT).show();
                    checks = false;
                }
                //Final check
                if(checks) {
                    Activity activity = getActivity();
                    LoggingInHelper.signUpUser(emailSignUp.getText().toString(), passwordSignUp.getText().toString(),
                            nameSignUp.getText().toString(), activity, appCompatActivity);
                }

            }
        });

        return view;
    }

}
