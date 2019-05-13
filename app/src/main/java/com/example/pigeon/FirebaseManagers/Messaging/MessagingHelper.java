package com.example.pigeon.FirebaseManagers.Messaging;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.pigeon.FirebaseManagers.Accounts.LoggingInHelper;
import com.example.pigeon.FirebaseManagers.FirebaseHelper;
import com.example.pigeon.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessagingHelper {


    //MESSAGES CAP = 30;
    public static MessageList<MessagingInstance> currentChatRoom;
    public static String currentChatID;

    /*
    *
    * An executorService is the executor service that executes all tasks here.
    * This requires an executor service attached to all the tasks in the returned list
    *
    * */
    public static List<Task<Void>> createChat(String otherUID){
        currentChatRoom = new MessageList();

        UUID uuid = UUID.randomUUID();
        currentChatID = uuid.toString();

        ChatInfo info = new ChatInfo("", "");

        ArrayList<Task<Void>> allTasks = new ArrayList<>();
        Task<Void> createNewMessagingArea = FirebaseHelper.messagingDB.getReference().child("Messages").child(currentChatID).setValue(true);
        Task<Void> createNewChatInfo = FirebaseHelper.messagingDB.getReference().child("Chats").child(currentChatID).setValue(info);
        Task<Void> createChatMember = FirebaseHelper.messagingDB.getReference().child("Chat Members").child(currentChatID).child(MainActivity.user.getuID()).setValue(true);
        Task<Void> createNewChatMember = FirebaseHelper.messagingDB.getReference().child("Chat Members").child(currentChatID).child(otherUID).setValue(true); //Add lists
        allTasks.add(createNewMessagingArea);
        allTasks.add(createNewChatInfo);
        allTasks.add(createChatMember);
        allTasks.add(createNewChatMember);
        currentChatRoom.addListener(new ListListener());
        MainActivity.user.addChat(currentChatID);


        FirebaseHelper.messagingDB.getReference().child("Messages").child(currentChatID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                MessagingInstance receivedMessage = dataSnapshot.getValue(TextMessage.class);
                if(receivedMessage.getType().equalsIgnoreCase("TEXT")){
                    if(!MainActivity.user.getuID().equalsIgnoreCase(receivedMessage.getUserID())){
                        TextMessage message = (TextMessage)receivedMessage;
                        currentChatRoom.add(message);
                        //update UI received here
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
            updatePreviousMessage(textmessage);
        }

    }

    private static void updatePreviousMessage(String message){
        HashMap<String, Object> map = new HashMap<>();
        map.put("previousMessage", message);
        FirebaseHelper.messagingDB.getReference().child("Chats").child(currentChatID).updateChildren(map);
    }


    public static class ChatInfo {
        String previousMessage;
        long TimeCreated;
        String title;

        public ChatInfo(String previousMessage, String title) {
            this.previousMessage = previousMessage;
            this.TimeCreated = System.currentTimeMillis();
            this.title = title;
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
           Task<Void> task = FirebaseHelper.messagingDB.getReference().child("Messages").child(currentChatID)
                    .push().setValue(currentChatRoom.peek());

           //Update UI Sending

           ExecutorService es = Executors.newSingleThreadExecutor();
           task.addOnCompleteListener(es, new OnCompleteListener<Void>() {
               @Override
               public void onComplete(@NonNull Task<Void> task) {
                    //Update UI delivered
               }
           });


        }
    }

}
