package com.example.pigeon.FirebaseManagers.Messaging;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.pigeon.FirebaseManagers.Accounts.LoggingInHelper;
import com.example.pigeon.FirebaseManagers.FirebaseHelper;
import com.example.pigeon.MainActivity;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class MessagingHelper {

    //MESGAES CAP = 30;
    public static MessageList<MessagingInstance> currentChatRoom;
    public static String currentChatID;

    /*
    *
    * An executorService is the executor service that executes all tasks here.
    * This requires an executor service attached to all the tasks in the returned list
    *
    * */
    public static List<Task<Void>> createSetup(String chatID, String otherUID){
        currentChatRoom = new MessageList();
        currentChatID = chatID;
        ArrayList<Task<Void>> allTasks = new ArrayList<>();
        Task<Void> createNewMessagingArea = FirebaseHelper.messagingDB.getReference().child("Messages").child(currentChatID).setValue(true);
        Task<Void> createNewChatInfo = FirebaseHelper.messagingDB.getReference().child("Chats").child(currentChatID).setValue(true);
        Task<Void> createChatMember = FirebaseHelper.messagingDB.getReference().child("Chat Members").child(currentChatID).child(MainActivity.user.getuID()).setValue(true);
        Task<Void> createNewChatMember = FirebaseHelper.messagingDB.getReference().child("Chat Members").child(currentChatID).child(otherUID).setValue(true);
        allTasks.add(createNewMessagingArea);
        allTasks.add(createNewChatInfo);
        allTasks.add(createChatMember);
        allTasks.add(createNewChatMember);
        currentChatRoom.addListener(new ListListener());

        FirebaseHelper.messagingDB.getReference().child("Messages").child(currentChatID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                MessagingInstance recievedMessage = dataSnapshot.getValue(MessagingInstance.class);
                if(recievedMessage.getType().equalsIgnoreCase("TEXT")){
                    if(MainActivity.user.getuID().equalsIgnoreCase(recievedMessage.getUserID())){
                        TextMessage message = (TextMessage)recievedMessage;
                        currentChatRoom.add(message);
                        //update UI here
                    }
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

            }
        });

        return allTasks;

    }


    public static void sendTextMessage(String textmessage){
        MessagingInstance message = MessagingFactory.initializeTextMessagingInstance(textmessage);
        if(currentChatRoom != null){
            currentChatRoom.offer(message);
        }
    }


    /*
    *
    * Turns out this will give an exception,
    * FIX: Try add new methods in the MessageList so that the client can use each method
    * based on whether or not the user is receiving messages Or sending them
    *
    * FIX: Switched the add method to not notify listeners
    *
    */
    static class ListListener implements MessageListListener{
        @Override
        public void OnMessageOffer() {
            //peek just looks at the top object and doesn't remove anything
            FirebaseHelper.messagingDB.getReference().child("Messages").child(currentChatID)
                    .push().setValue(currentChatRoom.peek());
            //Add UI update here

        }
    }

}
