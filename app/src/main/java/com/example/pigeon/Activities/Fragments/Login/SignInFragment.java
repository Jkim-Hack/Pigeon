package com.example.pigeon.Activities.Fragments.Login;

import android.app.Activity;
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

import com.example.pigeon.FirebaseManagers.Accounts.LoggingInHelper;
import com.example.pigeon.R;

public class SignInFragment extends Fragment {

    private EditText emailInput;
    private EditText passwordInput;
    private Button signIn;
    private Button forgotPass; //TODO: Implement forgot password

    //Creates a new Fragment for the pager adapter
    public static SignInFragment newInstance(int position){
        Bundle args = new Bundle();
        args.putInt("ARG_PAGE", position);
        SignInFragment signInFragment = new SignInFragment();
        signInFragment.setArguments(args);
        return signInFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sign_in_content, container, false);

        signIn = view.findViewById(R.id.signinbutton);
        emailInput = view.findViewById(R.id.emailInputSI);
        passwordInput = view.findViewById(R.id.passwordInputSI);

        //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        final AppCompatActivity appCompatActivity = (AppCompatActivity) this.getActivity(); //Gets the sign in activity
        final Activity activity = this.getActivity();
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoggingInHelper.signInUser(emailInput.getText().toString(), passwordInput.getText().toString(), activity, appCompatActivity); //Attempts to login
            }
        });



        return view;
    }
}
