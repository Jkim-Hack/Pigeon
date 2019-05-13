package com.example.pigeon.Activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.pigeon.FirebaseManagers.Accounts.LoggingInHelper;
import com.example.pigeon.FirebaseManagers.Accounts.User;
import com.example.pigeon.FirebaseManagers.FirebaseHelper;
import com.example.pigeon.FirebaseManagers.Messaging.MessagingHelper;
import com.example.pigeon.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
    public static User user; //The current user. Subject to change where this global variable should be placed
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseHelper.build();

       // LoggingInHelper.signInUser("jkim@gmail.com", "password");


    }

    public static void send(){
        List<Task<Void>> buildList = MessagingHelper.createChat("mwHaZIGmjrZTaIv1bidOAFEX8PA2");
        int numberOfThreads = 4;
        ExecutorService es = Executors.newFixedThreadPool(numberOfThreads);


        for (int i = 0; i < buildList.size()-1; i++) {
            buildList.get(i).addOnCompleteListener(es, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.w(TAG, "Success");
                    }
                }
            });
        }
        buildList.get(buildList.size()-1).addOnCompleteListener(es, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                MessagingHelper.sendTextMessage("Hello");
            }
        });

    }

}
