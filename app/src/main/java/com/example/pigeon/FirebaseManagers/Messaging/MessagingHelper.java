package com.example.pigeon.FirebaseManagers.Messaging;

import com.example.pigeon.FirebaseManagers.FirebaseHelper;
import com.example.pigeon.MainActivity;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class MessagingHelper {

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
    */
    static class ListListener implements MessageListListener{
        @Override
        public void OnMessageAdd() {
            //peek just looks at the top object and doesn't remove anything
            FirebaseHelper.messagingDB.getReference().child("Messages").child(currentChatID)
                    .push().setValue(currentChatRoom.peek());


        }
    }

}
