package com.example.pigeon.FirebaseManagers.Messaging;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.pigeon.Activities.Adapters.MessageListAdapter;
import com.example.pigeon.Activities.MainMenuActivity;
import com.example.pigeon.Activities.MessagingRoomActivity;
import com.example.pigeon.FirebaseManagers.FirebaseHelper;
import com.example.pigeon.Activities.MainActivity;
import com.example.pigeon.FirebaseManagers.ImageHandler;
import com.example.pigeon.FirebaseManagers.LoggerHelper;
import com.example.pigeon.common.LogEntry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.content.ContentValues.TAG;

public class MessagingHelper {

    //TODO: ENABLE OFFLINE CAPABILITIES https://firebase.google.com/docs/database/android/offline-capabilities

    //MESSAGES CAP = 30;
    public static HashMap<String, ChatInfo> chatList = new HashMap<>(); //List of chat rooms displayed in the main menu
    public static List<UploadTask> uploadTasks; //upload tasks for the current viewed chat room
    public static HashMap<String, MessageList<MessagingInstance>> chatrooms = new HashMap(); //Current chat room's message list
    public static HashMap<String, MessageListAdapter> adapters = new HashMap<>();
    public static String currentChatID; //Current chat room's ID
    public static HashMap<String, HashMap<String, String>> chatMemebers = new HashMap<>(); //The chat members per chat. key1 = chatID, key2 = userId, value2 = name
    public static HashMap<String, ChatInfo> tempChatList = new HashMap(); //Temporary container for new chats


    public static final Integer CREATECHAT = 1;
    public static final String TIMESTAMP = "timestamp";
    public static final String TITLE = "title";

    /*
     *
     * This creates a new chat and updates Firebase
     *
     * An executorService is the executor service that executes all tasks here.
     * This requires an executor service attached to all the tasks in the returned list
     *
     *
     * */
    //Creates a new chat
    public static void createChat(Collection<String> otherUIDs, final Context context) {
        LoggerHelper.sendLog(new LogEntry("Attempting to create chat...", MainActivity.user.getClientNum()));
        StringBuilder sb = new StringBuilder();
        if (otherUIDs.size() > 1) {
            Iterator<String> uids = otherUIDs.iterator();
            while (uids.hasNext()) {
                sb.append(uids.next());
                if (uids.hasNext()) {
                    sb.append(",");
                }
            }
        } else {
            sb.append(otherUIDs.iterator().next());
        }

        final Long time = System.currentTimeMillis();

        HashMap command = new HashMap();
        command.put(FirebaseHelper.COMMAND, CREATECHAT);
        command.put(FirebaseHelper.CHATUSERS, sb.toString());
        command.put(TIMESTAMP, time);

        LoggerHelper.sendLog(new LogEntry("Requesting chat creation...", MainActivity.user.getClientNum()));

        final Query commandI = FirebaseHelper.mainDB.getReference(FirebaseHelper.commandInbox).child(MainActivity.user.getClientNum()).push();
        ((DatabaseReference) commandI).setValue(command);

        commandI.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                //TODO: The chat members dont show when u start this activity. FIX
                Intent intent = new Intent(context, MessagingRoomActivity.class);
                context.startActivity(intent);
                commandI.removeEventListener(this);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getDetails());
            }
        });

        FirebaseHelper.mainDB.getReference().child(MainActivity.user.getuID()).child(FirebaseHelper.CHATLIST).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    String uid = (String) dataSnapshot.getValue();
                    if (chatrooms.containsKey(uid)) {
                        currentChatID = uid;
                        FirebaseHelper.mainDB.getReference().child(MainActivity.user.getuID()).child(FirebaseHelper.CHATLIST).removeEventListener(this);
                    }

                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                //TODO: ADD WHEN CHAT IS REMOVED
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getDetails());
            }
        });


    }

    public static void createMessagingListener(String chatID) {
        final String chatUUID = chatID;

        //Adds a child event listener so that every time a new message is added, the onChildAdded method is called.
        FirebaseHelper.messagingDB.getReference().child("Messages").child(chatUUID).orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    //Creates a new MessageList and copies all the messages received into the new list.
                    System.out.println(dataSnapshot.toString());
                    HashMap messagingInstanceMap = (HashMap) dataSnapshot.getValue();
                    MessagingInstance messagingInstance = null;

                    String type = (String) messagingInstanceMap.get("type");
                    switch (type) {
                        case "TEXT":
                            String message = (String) messagingInstanceMap.get("message");
                            String uid = (String) messagingInstanceMap.get("userID");
                            Long timestamp = (Long) messagingInstanceMap.get("sentTimestamp");
                            messagingInstance = new TextMessage(message, uid, timestamp);
                            updatePreviousMessage(message, chatUUID);
                            break;
                    }

                    if (!messagingInstance.getUserID().equals(MainActivity.user.getuID())) {
                        //TODO: Add notification here
                    }
                    System.out.println(messagingInstance);
                    chatrooms.get(chatUUID).add(messagingInstance);
                    adapters.get(chatUUID).add(messagingInstance);
                } else {
                    System.out.println(false);
                }
                LoggerHelper.sendLog(new LogEntry("Message received---ChatID: " + chatUUID, MainActivity.user.getClientNum()));

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

            }

        });
    }


    /*
     * Loads all of the chat rooms and any new chats are added to the variable "chatList".
     * This should NOT be used to enter a chat room.
     */
    public static void LoadAllChatRooms(final ArrayAdapter adapter) {
        try {
            HashMap map = MainActivity.user.getChatMap();
            if (map.isEmpty())
                return;
        } catch (NullPointerException e) {
            return;
        }
        adapter.clear();
        final HashMap<String, String> chatIDLists = MainActivity.user.getChatMap();
        System.out.println(chatIDLists);

        Set set = chatIDLists.entrySet();
        Iterator<Map.Entry<String, String>> iterator = set.iterator();
        while (iterator.hasNext()) {
            final String chatID = iterator.next().getValue();
            FirebaseHelper.messagingDB.getReference("Chats").child(chatID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        ChatInfo chatInfo = dataSnapshot.getValue(ChatInfo.class);
                        updateChatList(chatID, chatInfo, adapter);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w(TAG, databaseError.getDetails());
                }
            });

        }


    }


    //Creates a map then adds the map into the chatList
    private static void updateChatList(String chatID, ChatInfo chatInfo, ArrayAdapter adapter) {
        HashMap<String, ChatInfo> map = new HashMap<>();
        map.put(chatID, chatInfo);
        adapter.add(map);
        chatList.put(chatID, chatInfo);
    }

    //Loads a single chat room. This should be used only for entering a chat room within the app.
    public static void LoadChatRoom(final String chatID, final Activity activity, Context context) {
        currentChatID = chatID;

        String storagePath = "Messaging Rooms/" + currentChatID + "images/messages/";
        StorageReference reference = FirebaseHelper.mainStorage.getReference().child(storagePath);
        uploadTasks = reference.getActiveUploadTasks();

        MessageList messageList = chatrooms.get(currentChatID);
        System.out.println(messageList + "FFFF");
        System.out.println(adapters.get(currentChatID) + "RRRR");
        if (adapters.get(currentChatID).isEmpty()) {
            adapters.get(currentChatID).addList(messageList);
        }

        ChatInfo chatInfo = chatList.get(chatID);
        System.out.println(chatInfo.title);
        MessagingRoomActivity.setChatInfo(chatInfo);

        Intent intent = new Intent(activity, MessagingRoomActivity.class);
        activity.startActivity(intent);

    }

    //Sends a basic text message
    public static void sendTextMessage(String textmessage) {
        MessagingInstance message = MessagingFactory.initializeTextMessagingInstance(textmessage); //Creates a new text message object
        if (chatrooms != null) {
            chatrooms.get(currentChatID).offer(message); //Sends message into the messageList
        }

    }

    //TODO: NEEDS TESTING
    //Sends an image message from the device's existing images.
    public static void sendImageMessage(String src) {

        //The storage path and file name will be based on the timestamp sent.
        long timestamp = System.currentTimeMillis();
        String storagePath = "Messaging Rooms/" + currentChatID + "images/messages/" + timestamp + ".jpg";

        //Initialize a new message with the image reference download url as the text
        StorageReference imageRef = FirebaseHelper.mainStorage.getReference().child(storagePath); //Image reference
        final String downloadURL = imageRef.getDownloadUrl().toString(); //Get the reference downloadURL
        final MessagingInstance message = MessagingFactory.initializeImageMessagingInstance(downloadURL); //Create a new message

        //Create meta data for the image
        StorageMetadata imageMetaData = new StorageMetadata.Builder()
                .setContentType("images/jpg") //sets the type of file being sent
                .build();

        //Creates a new upload task created by the ImageHandler
        UploadTask uploadTask = ImageHandler.uploadImagePath(src, storagePath, imageMetaData);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) { //Handles failed upload
                //TODO: Handle unsuccessful uploads
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() { //Handles paused upload
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                System.out.println("Upload is paused");
                //TODO: FRONTEND update pause event
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() { //Handles in progress upload
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                System.out.println("Upload is" + progress + "% done");
                //TODO: FRONTEND display the progress here
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() { //Handles successful upload
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                if (chatrooms != null) {
                    chatrooms.get(currentChatID).offer(message);
                    updatePreviousMessage(downloadURL, currentChatID);
                }

            }
        });
    }

    //TODO: FINISH METHOD
    //Sends an image message based on Android's ImageView. This should be used to take pictures directly from the app.
    //SHOULD NOT BE USED FOR EXISTING IMAGES.
    public static void sendImageMessage(ImageView image) {
    }


    //Updates "previous message" in the database
    private static void updatePreviousMessage(String message, String chatID) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("previousMessage", message);
        FirebaseHelper.messagingDB.getReference().child("Chats").child(chatID).updateChildren(map);
    }


    //The chat's information
    public static class ChatInfo {
        public String previousMessage;
        public long TimeCreated;
        public String title;

        public ChatInfo() {
            this.previousMessage = "no messages";
            this.TimeCreated = System.currentTimeMillis();
            this.title = "title";
        }

        public ChatInfo(String previousMessage, String title) {
            this.previousMessage = previousMessage;
            this.TimeCreated = System.currentTimeMillis();
            this.title = title;
        }

        public ChatInfo(Long timeCreated, String previousMessage, String title) {
            this.previousMessage = previousMessage;
            this.TimeCreated = timeCreated;
            this.title = title;
        }

    }

    /*
     * The listener for adding a new message into the current chat message list
     */
    public static class ListListener implements MessageListListener {
        @Override
        public void OnMessageOffer() {
            //adapters.get(currentChatID).add(chatrooms.get(currentChatID).getLast());
            System.out.println(chatrooms.get(currentChatID).getLast().getSentTimestamp());
            //peek just looks at the top object and doesn't remove anything
            Task<Void> task = FirebaseHelper.messagingDB.getReference().child("Messages").child(currentChatID)
                    .push().setValue(chatrooms.get(currentChatID).getLast());
            //Update UI Sending

            ExecutorService es = Executors.newSingleThreadExecutor();
            task.addOnCompleteListener(es, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    //Update UI delivered
                    System.out.println("Delivered");
                }
            });


        }
    }

}
