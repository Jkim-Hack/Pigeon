package com.example.pigeon.FirebaseManagers.Messaging;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.pigeon.FirebaseManagers.FirebaseHelper;
import com.example.pigeon.Activities.MainActivity;
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

    //TODO: ENABLE OFFLINE CAPABILITIES https://firebase.google.com/docs/database/android/offline-capabilities

    //MESSAGES CAP = 30;
    public static ArrayList<HashMap<String, ChatInfo>> chatList = new ArrayList<>();
    public static MessageList<MessagingInstance> currentChatRoom;
    public static String currentChatID;

    /*
     *
     * This creates a new chat and updates firebase
     *
     * An executorService is the executor service that executes all tasks here.
     * This requires an executor service attached to all the tasks in the returned list
     *
     *
     * */
    public static List<Task<Void>> createChat(String otherUID) {
        currentChatRoom = new MessageList();

        //Creates a new chat id
        UUID uuid = UUID.randomUUID();
        currentChatID = uuid.toString();

        //Default chatinfo object
        ChatInfo info = new ChatInfo("", "");

        ArrayList<Task<Void>> allTasks = new ArrayList<>();

        //Creates all necessary information for a chat and puts them into a task
        Task<Void> createNewMessagingArea = FirebaseHelper.messagingDB.getReference().child("Messages").child(currentChatID).setValue(true);
        Task<Void> createNewChatInfo = FirebaseHelper.messagingDB.getReference().child("Chats").child(currentChatID).setValue(info);
        Task<Void> createChatMember = FirebaseHelper.messagingDB.getReference().child("Chat Members").child(currentChatID).child(MainActivity.user.getuID()).setValue(true);
        Task<Void> createNewChatMember = FirebaseHelper.messagingDB.getReference().child("Chat Members").child(currentChatID).child(otherUID).setValue(true);

        //Adds all necessary tasks to the task pool
        allTasks.add(createNewMessagingArea);
        allTasks.add(createNewChatInfo);
        allTasks.add(createChatMember);
        allTasks.add(createNewChatMember);

        //Adds a new message listener for the created chatroom
        currentChatRoom.addListener(new ListListener());

        //Finally adds the chat and associates it with the user
        MainActivity.user.addChat(currentChatID);

        //Adds a child event listener so that every time a new message is added, the onChildAdded method is called.
        FirebaseHelper.messagingDB.getReference().child("Messages").child(currentChatID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //Gets the messaging instance for a text message
                MessagingInstance receivedMessage = dataSnapshot.getValue(TextMessage.class);
                if(dataSnapshot.exists()){
                    if (receivedMessage.getType().equalsIgnoreCase("TEXT")) {
                        if (!MainActivity.user.getuID().equalsIgnoreCase(receivedMessage.getUserID())) {
                            TextMessage message = (TextMessage) receivedMessage;
                            currentChatRoom.add(message);
                            //update UI received here
                            //Notification here
                        }
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


    /*
     * Loads all of the chat rooms and any new chats are added to the variable "chatList"
     */
    public static void LoadAllChatRooms() {
       final ArrayList<String> chatIDLists = MainActivity.user.getChatList();
        for (final String chatID : chatIDLists) {
            FirebaseHelper.messagingDB.getReference("Chats").child(chatID).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if (dataSnapshot.exists()) {
                        updateChatList(chatID, dataSnapshot.getValue(ChatInfo.class));
                    }
                }
                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    //TODO: ADD UPDATE CHAT
                }
                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    //TODO: Add chat removed event
                }
                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    //SHOULD NEVER BE USED
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    //TODO: ADD CANCELLED EVENT
                }
            });
        }
    }

    //Creates a map then adds the map into the chatList
    private static void updateChatList(String chatID, ChatInfo chatInfo){
        HashMap<String, ChatInfo> map = new HashMap<>();
        map.put(chatID, chatInfo);
        chatList.add(map);
    }

    public static void LoadChatRoom(String chatID) {
        currentChatRoom = new MessageList<>();
        currentChatID = chatID;

        FirebaseHelper.messagingDB.getReference("Messages").child(currentChatID).orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    List<MessagingInstance> messagingInstanceList = (List<MessagingInstance>) dataSnapshot.getValue();
                    //TODO: ADD currentChatRoom TO MESSAGING INSTANCE LIST
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

    }


    //Sends a basic text message
    public static void sendTextMessage(String textmessage) {
        MessagingInstance message = MessagingFactory.initializeTextMessagingInstance(textmessage);
        if (currentChatRoom != null) {
            currentChatRoom.offer(message);
            updatePreviousMessage(textmessage);
        }

    }

    //Updates "previous message" in the database
    private static void updatePreviousMessage(String message) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("previousMessage", message);
        FirebaseHelper.messagingDB.getReference().child("Chats").child(currentChatID).updateChildren(map);
    }


    //The chat's information
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
    static class ListListener implements MessageListListener {
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
