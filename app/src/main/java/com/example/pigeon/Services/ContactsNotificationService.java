package com.example.pigeon.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.util.Log;

import com.example.pigeon.Activities.MainActivity;
import com.example.pigeon.FirebaseManagers.Accounts.ContactsHelper;
import com.example.pigeon.FirebaseManagers.FirebaseHelper;
import com.example.pigeon.FirebaseManagers.LoggerHelper;

import com.example.pigeon.FirebaseManagers.Messaging.MessagingHelper;
import com.example.pigeon.common.LogEntry;
import com.example.pigeon.common.NotificationHelper;
import com.example.pigeon.common.UserInfo.ContactInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

public class ContactsNotificationService extends Service {

    private Query requestListenerThread;
    private ChildEventListener requestChildEventListener;

    private Query acceptedListenerThread;
    private ChildEventListener acceptedChildEventListener;

    TimerTask timerTask;
    Timer timer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(MainActivity.user != null){
            addContactRequestListener();
            addContactAcceptedListener();
        }

        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if(MainActivity.user != null){
                    addContactAcceptedListener();
                    addContactRequestListener();
                    timerTask.cancel();
                    timer = null;
                }
            }
        };
        timer.schedule(timerTask, 500, 100);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if(requestListenerThread != null){
            if(requestChildEventListener != null){
                requestListenerThread.removeEventListener(requestChildEventListener);
                requestChildEventListener = null;
            }
            requestListenerThread = null;
        }

        if(acceptedListenerThread != null){
            if(acceptedChildEventListener != null){
                acceptedListenerThread.removeEventListener(acceptedChildEventListener);
                acceptedChildEventListener = null;
            }
            acceptedChildEventListener = null;
        }

        if (timer != null) {
            timerTask.cancel();
            timer = null;
        }
        super.onDestroy();
    }

    private void addContactRequestListener() {
        final Context context = this;
        requestListenerThread = FirebaseHelper.notifsDB.getReference().child(MainActivity.user.getuID()).child(ContactsHelper.CONTACTREQUESTS);
        requestChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    System.out.println(dataSnapshot.getKey());
                    ContactInfo contactInfo = dataSnapshot.getValue(ContactInfo.class);
                    NotificationHelper.sendContactRequestNotification(contactInfo, context);
                } else {
                    System.out.println(false);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getDetails()); //System log
                LoggerHelper.sendLog(new LogEntry(databaseError.getDetails(), MainActivity.user.getClientNum())); //Sends details to server
            }
        };

        requestListenerThread.addChildEventListener(requestChildEventListener);
    }

    private void addContactAcceptedListener() {
        final Context context = this;
        acceptedListenerThread = FirebaseHelper.notifsDB.getReference().child(MainActivity.user.getuID()).child(ContactsHelper.CONTACTSLIST);
        acceptedChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    System.out.println(dataSnapshot.getKey());
                    ContactInfo contactInfo = dataSnapshot.getValue(ContactInfo.class);
                    NotificationHelper.sendContactAcceptedNotification(contactInfo, context);
                } else {
                    System.out.println(false);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getDetails()); //System log
                LoggerHelper.sendLog(new LogEntry(databaseError.getDetails(), MainActivity.user.getClientNum())); //Sends details to server
            }
        };

        acceptedListenerThread.addChildEventListener(acceptedChildEventListener);
    }
}
