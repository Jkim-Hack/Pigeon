package com.example.pigeon.Activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.pigeon.FirebaseManagers.Accounts.LoggingInHelper;
import com.example.pigeon.FirebaseManagers.FirebaseHelper;
import com.example.pigeon.Services.ChatNotificationService;
import com.example.pigeon.Services.ContactsNotificationService;
import com.example.pigeon.common.SavedSharedPreferences;

public class StartingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        FirebaseHelper.build();

        if(SavedSharedPreferences.getEmail(StartingActivity.this).length() == 0 || SavedSharedPreferences.getPassword(StartingActivity.this).length() == 0){
            Intent intent = new Intent(this, SignInActivity.class);
            this.startActivity(intent);
        } else {
            String email = SavedSharedPreferences.getEmail(StartingActivity.this);
            String pass = SavedSharedPreferences.getPassword(StartingActivity.this);

            LoggingInHelper.signInUser(email, pass, this, this);
        }

        if (!isMyServiceRunning()){
            System.out.println("-102-120-102-10-201-201-02-");
            Intent serviceIntent = new Intent(this, ChatNotificationService.class);
            this.startService(serviceIntent);
            Intent contactServiceIntent = new Intent(this, ContactsNotificationService.class);
            this.startService(contactServiceIntent);
        }

        super.onCreate(savedInstanceState);
    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (ChatNotificationService.class.getName().equals(service.service.getClassName()) && ContactsNotificationService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}
