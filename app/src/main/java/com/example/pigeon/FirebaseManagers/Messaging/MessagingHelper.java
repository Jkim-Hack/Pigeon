package com.example.pigeon.FirebaseManagers.Messaging;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.example.pigeon.common.UserInfo.ContactInfo;
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.content.ContentValues.TAG;
import static com.example.pigeon.Activities.MainMenuActivity.chatListAdapter;

public class MessagingHelper {

    //TODO: ENABLE OFFLINE CAPABILITIES https://firebase.google.com/docs/database/android/offline-capabilities

    //MESSAGES CAP = 30;
    public static HashMap<String, ChatInfo> chatList = new HashMap<>(); //List of chat rooms displayed in the main menu
    public static List<UploadTask> uploadTasks; //upload tasks for the current viewed chat room
    public static HashMap<String, MessageList<MessagingInstance>> chatrooms = new HashMap(); //Current chat room's message list
    public static HashMap<String, MessageListAdapter> adapters = new HashMap<>();
    public static String currentChatID; //Current chat room's ID
    public static HashMap<String, HashMap<String, ContactInfo>> chatMembers = new HashMap<>(); //The chat members per chat. key1 = chatID, key2 = userId, value2 = name
    public static HashMap<String, ChatInfo> tempChatList = new HashMap(); //Temporary container for new chats


    public static final Integer CREATECHAT = 1;
    public static final String TITLE = "title";

    //Creates a new chat
    public static void createChat(Collection<String> otherUIDs, final Context context) {
        LoggerHelper.sendLog(new LogEntry("Attempting to create chat...", MainActivity.user.getClientNum())); //Pushes log to server

        //Creates a string with commas from the collection
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

        final Long time = System.currentTimeMillis(); //Gets time

        //Creates a hash map for a command/request to the server
        HashMap command = new HashMap();
        command.put(FirebaseHelper.COMMAND, CREATECHAT); //The command number
        command.put(FirebaseHelper.CHATUSERS, sb.toString()); //The chat users
        command.put(FirebaseHelper.TIMESTAMP, time); //The time

        LoggerHelper.sendLog(new LogEntry("Requesting chat creation...", MainActivity.user.getClientNum())); //Pushes log to server

        //Push the command into Firebase
        final Query commandI = FirebaseHelper.mainDB.getReference(FirebaseHelper.commandInbox).child(MainActivity.user.getClientNum()).push();
        ((DatabaseReference) commandI).setValue(command);

        //Command listener
        commandI.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String key = dataSnapshot.getKey(); //Gets the key from the removed value in the command inbox
                System.out.println(key);
                //Creates a new listener to the chat list of the current user
                FirebaseHelper.mainDB.getReference().child(MainActivity.user.getuID()).child(FirebaseHelper.CHATLIST).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if (dataSnapshot.exists()) {
                            String uid = (String) dataSnapshot.getValue(); //Gets the uid
                            if (dataSnapshot.getKey().equals(key)) { //Checks if the uid is matching with the one associated with the key
                                currentChatID = uid; //Switch to the current chatID
                                getChatMembersInit(currentChatID, context); //Gets the chat members and switches activities
                                updateChatInfo(uid);
                                FirebaseHelper.mainDB.getReference().child(MainActivity.user.getuID()).child(FirebaseHelper.CHATLIST).removeEventListener(this); //Finally removes this listener

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
                        Log.e(TAG, databaseError.getDetails()); //System log
                        LoggerHelper.sendLog(new LogEntry(databaseError.getDetails(), MainActivity.user.getClientNum())); //Sends details to server
                    }
                });

                commandI.removeEventListener(this); //Remove the listener from the command inbox
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getDetails()); //System log
                LoggerHelper.sendLog(new LogEntry(databaseError.getDetails(), MainActivity.user.getClientNum())); //Sends details to server
            }
        });



    }

    //TODO: CREATE A MORE EFFICIENT METHOD OF REPLACING
    //TODO: Make comments
    private static void updateChatInfo(String chatID){
        final String chatUUID = chatID;
        FirebaseHelper.messagingDB.getReference().child("Chats").child(chatID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String key = dataSnapshot.getKey();
                String changed = dataSnapshot.getValue(String.class);
                ChatInfo chatInfo = null;
                if (chatListAdapter != null) {
                    //Puts data into a map
                    for(int i = 0; i < chatListAdapter.getCount(); i++){
                        if(chatListAdapter.getItem(i).get(chatUUID) != null){
                            chatInfo = new ChatInfo(chatListAdapter.getItem(i).get(chatUUID));
                            switch (key) {
                                case "previousMessage":
                                    chatInfo.previousMessage = changed;
                                    break;
                                case "title":
                                    chatInfo.title = changed;
                                    break;
                            }
                            chatListAdapter.remove(chatListAdapter.getItem(i));

                            HashMap<String, ChatInfo> map = new HashMap<>();
                            map.put(chatUUID, chatInfo);
                            chatListAdapter.add(map);
                        }
                    }
                    if(chatInfo != null){
                        //Adds chat info into chat list
                        MessagingHelper.chatList.replace(chatUUID, chatInfo);
                        chatListAdapter.notifyDataSetChanged();
                    }
                } else {
                    if(chatInfo != null) {
                        //Adds to the temporary chat list
                        MessagingHelper.tempChatList.put(chatUUID, chatInfo);
                    }
                }
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
    *
    * Gets all the chat members within the chatID, this should only be used once per chat as
    * it is only to make sure all the information is put into our data maps (chatMembers map), and finally switch activities.
    *
    */
    private static void getChatMembersInit(final String chatID, final Context context) {
        final String chatUUID = chatID;

        //Create a listener for the chat members with the specified ID
        FirebaseHelper.messagingDB.getReference().child("Chat Members").child(chatUUID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (MessagingHelper.chatMembers.get(chatUUID) == null || MessagingHelper.chatMembers.get(chatUUID).isEmpty()) { //Checks if this value is null/empty
                    HashMap<String, ContactInfo> member = new HashMap<>(); //Create a new hash map
                    member.put(dataSnapshot.getKey(), dataSnapshot.getValue(ContactInfo.class)); //Add values into map
                    MessagingHelper.chatMembers.put(chatUUID, member);
                } else {
                    HashMap<String, ContactInfo> members = MessagingHelper.chatMembers.get(chatUUID); //Create a new hash map
                    members.put(dataSnapshot.getKey(), dataSnapshot.getValue(ContactInfo.class)); //Add values into map
                }
                MainMenuActivity.chatListAdapter.notifyDataSetChanged(); //Notify the chatListAdapter specified in the MainMenuActivity that an item has changed
                Intent intent = new Intent(context, MessagingRoomActivity.class); //Create a new intent
                context.startActivity(intent); //Start the activity

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

    //Creates a new listener for messages between users
    public static void createMessagingListener(String chatID, final Context context) {
        final String chatUUID = chatID;

        //Adds a child event listener so that every time a new message is added, the onChildAdded method is called.
        FirebaseHelper.messagingDB.getReference().child("Messages").child(chatUUID).orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
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
                            updatePreviousMessage(message, chatUUID); //Updates previous message
                            break;
                    }

                    chatrooms.get(chatUUID).add(messagingInstance); //Add message to the chatroom
                    adapters.get(chatUUID).add(messagingInstance); //Adds message to the MessageListAdapter
                } else {
                    System.out.println(false);
                }
                LoggerHelper.sendLog(new LogEntry("Message received---ChatID: " + chatUUID, MainActivity.user.getClientNum())); //Sends log

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


    /*
     * Loads all of the chat rooms and any new chats are added to the variable "chatList".
     * This should NOT be used to enter a chat room.
     */
    public static void LoadAllChatRooms(final ArrayAdapter adapter) {
        //Checks if the map is null/empty then just returns
        try {
            HashMap map = MainActivity.user.getChatMap();
            if (map.isEmpty())
                return;
        } catch (NullPointerException e) {
            return;
        }
        adapter.clear(); //Clear everything in the adapter

        final HashMap<String, String> chatIDLists = MainActivity.user.getChatMap(); //Gets the current chats
        System.out.println(chatIDLists);

        Set set = chatIDLists.entrySet();
        Iterator<Map.Entry<String, String>> iterator = set.iterator();

        //Iterate through the map of entries
        while (iterator.hasNext()) {
            final String chatID = iterator.next().getValue(); //Gets chatID
            FirebaseHelper.messagingDB.getReference("Chats").child(chatID).addListenerForSingleValueEvent(new ValueEventListener() { //Gets ChatInfo's for each chatID
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        ChatInfo chatInfo = dataSnapshot.getValue(ChatInfo.class);
                        updateChatList(chatID, chatInfo, adapter); //Updates the chatList
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, databaseError.getDetails()); //System log
                    LoggerHelper.sendLog(new LogEntry(databaseError.getDetails(), MainActivity.user.getClientNum())); //Sends details to server
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

    /*
    *
    * Loads a single chat room. This should be used only for entering a chat room within the app.
    * Can use either context or activity, but one or the other should be null
    *
    */
    public static void LoadChatRoom(final String chatID, Activity activity, Context context) {
        currentChatID = chatID;

        /* Needed for images
        String storagePath = "Messaging Rooms/" + currentChatID + "images/messages/";
        StorageReference reference = FirebaseHelper.mainStorage.getReference().child(storagePath);
        uploadTasks = reference.getActiveUploadTasks();
        */

        MessageList messageList = chatrooms.get(currentChatID);
        System.out.println(messageList + "FFFF");
        System.out.println(adapters.get(currentChatID) + "RRRR");
        if (adapters.get(currentChatID).isEmpty()) {
            adapters.get(currentChatID).addList(messageList);
        }

        ChatInfo chatInfo = chatList.get(chatID);
        System.out.println(chatInfo.title);
        MessagingRoomActivity.setChatInfo(chatInfo);

        if(activity != null){
            Intent intent = new Intent(activity, MessagingRoomActivity.class);
            activity.startActivity(intent);
        } else if(context != null){
            Intent intent = new Intent(context, MessagingRoomActivity.class);
            activity.startActivity(intent);
        }

    }

    public static void LoadChatRoom(final String chatID) {
        currentChatID = chatID;

        /* Needed for images
        String storagePath = "Messaging Rooms/" + currentChatID + "images/messages/";
        StorageReference reference = FirebaseHelper.mainStorage.getReference().child(storagePath);
        uploadTasks = reference.getActiveUploadTasks();
        */

        MessageList messageList = chatrooms.get(currentChatID);
        System.out.println(messageList + "FFFF");
        System.out.println(adapters.get(currentChatID) + "RRRR");
        if (adapters.get(currentChatID).isEmpty()) {
            adapters.get(currentChatID).addList(messageList);
        }

        ChatInfo chatInfo = chatList.get(chatID);
        System.out.println(chatInfo.title);
        MessagingRoomActivity.setChatInfo(chatInfo);

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

        public ChatInfo(ChatInfo info){
            this.previousMessage = info.previousMessage;
            this.TimeCreated = info.TimeCreated;
            this.title = info.title;
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
