package com.example.pigeon.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Person;
import android.util.Log;

import com.example.pigeon.Activities.MainActivity;
import com.example.pigeon.FirebaseManagers.FirebaseHelper;
import com.example.pigeon.FirebaseManagers.LoggerHelper;
import com.example.pigeon.FirebaseManagers.Messaging.MessagingHelper;
import com.example.pigeon.FirebaseManagers.Messaging.MessagingInstance;
import com.example.pigeon.FirebaseManagers.Messaging.Text.TextMessage;
import com.example.pigeon.common.UserInfo.ContactInfo;
import com.example.pigeon.common.LogEntry;
import com.example.pigeon.common.NotificationHelper;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;
import static com.example.pigeon.FirebaseManagers.Messaging.MessagingHelper.chatMembers;

public class ChatNotificationService extends Service {

    TimerTask timerTask;
    Timer timer;
    private int currAmt;

    private List<String> chatIDs;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        currAmt = 0;
        chatIDs = new ArrayList<>();
        if (MainActivity.user != null) {
            currAmt = MessagingHelper.chatList.size();
            Iterator<String> iterator = MessagingHelper.chatList.keySet().iterator();
            while (iterator.hasNext()) {
                String id = iterator.next();
                chatIDs.add(id);
                //Add notification setup here
                addChatListener(id);
            }
        }

        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (currAmt != MessagingHelper.chatList.size()) {
                    HashMap<String, MessagingHelper.ChatInfo> map = MessagingHelper.chatList;
                    Iterator<String> iterator1 = map.keySet().iterator();
                    while (iterator1.hasNext()) {
                        String id = iterator1.next();
                        if (!chatIDs.contains(id)) {
                            //Add notification setup here
                            addChatListener(id);
                        }
                    }
                    currAmt = MessagingHelper.chatList.size();
                }

            }
        };
        timer.schedule(timerTask, 500, 100);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (timer != null) {
            timerTask.cancel();
            timer = null;
        }
        super.onDestroy();
    }

    private void addChatListener(final String chatUUID) {
        final Context context = this;
        System.out.println(chatUUID);
        FirebaseHelper.notifsDB.getReference().child(MainActivity.user.getuID()).child(chatUUID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    System.out.println(dataSnapshot.getKey());

                    //Creates a new MessageList and copies all the messages received into the new list.
                    HashMap messagingInstanceMap = (HashMap) dataSnapshot.getValue();
                    MessagingInstance messagingInstance = null;
                    String type = (String) messagingInstanceMap.get("type"); //Gets the type of message (TEXT, IMAGE)
                    String message = null;
                    String uid = null;
                    Long timestamp = null;
                    switch (type) {
                        case "TEXT":
                            //Gets all info
                            message = (String) messagingInstanceMap.get("message");
                            uid = (String) messagingInstanceMap.get("userID");
                            timestamp = (Long) messagingInstanceMap.get("sentTimestamp");
                            messagingInstance = new TextMessage(message, uid, timestamp);
                            break;
                    }

                    if (!messagingInstance.getUserID().equals(MainActivity.user.getuID())) {
                        System.out.println(((TextMessage) messagingInstance).getMessage());
                        ContactInfo contactInfo = chatMembers.get(chatUUID).get(messagingInstance.getUserID());
                        Person person = contactInfo.buildPerson();
                        NotificationHelper.sendMessagingNotification(chatUUID, message, person, timestamp, context);
                    }
                } else {
                    System.out.println(false);
                }
                //TODO: ADD FOR IMAGE MESSAGE TYPES TOO
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

        });

    }

}
