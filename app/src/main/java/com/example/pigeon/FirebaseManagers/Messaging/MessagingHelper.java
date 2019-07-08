package com.example.pigeon.FirebaseManagers.Messaging;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.pigeon.FirebaseManagers.FirebaseHelper;
import com.example.pigeon.Activities.MainActivity;
import com.example.pigeon.FirebaseManagers.ImageHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessagingHelper {

    //TODO: ENABLE OFFLINE CAPABILITIES https://firebase.google.com/docs/database/android/offline-capabilities

    //MESSAGES CAP = 30;
    public static ArrayList<HashMap<String, ChatInfo>> chatList = new ArrayList<>(); //List of chat rooms displayed in the main menu
    public static List<UploadTask> uploadTasks; //upload tasks for the current viewed chat room
    public static MessageList<MessagingInstance> currentChatRoom; //Current chat room's message list
    public static String currentChatID; //Current chat room's ID

    /*
     *
     * This creates a new chat and updates Firebase
     *
     * An executorService is the executor service that executes all tasks here.
     * This requires an executor service attached to all the tasks in the returned list
     *
     *
     * */

    //TODO: NEEDS TESTING
    //Creates a new chat
    public static List<Task<Void>> createChat(String otherUID) {
        currentChatRoom = new MessageList();

        //Creates a new chat id
        UUID uuid = UUID.randomUUID();
        currentChatID = uuid.toString();

        //Default chatinfo object
        ChatInfo info = new ChatInfo("test", "uu");

        ArrayList<Task<Void>> allTasks = new ArrayList<>();

        //Creates all necessary information for a chat and puts them into a task
        //Task<Void> createNewMessagingArea = FirebaseHelper.messagingDB.getReference().child("Messages").child(currentChatID).setValue(true);
        Task<Void> createNewChatInfo = FirebaseHelper.messagingDB.getReference().child("Chats").child(currentChatID).setValue(info);
        Task<Void> createChatMember = FirebaseHelper.messagingDB.getReference().child("Chat Members").child(currentChatID).child(MainActivity.user.getuID()).setValue(true);
        Task<Void> createNewChatMember = FirebaseHelper.messagingDB.getReference().child("Chat Members").child(currentChatID).child(otherUID).setValue(true);

        //Adds all necessary tasks to the task pool
        //allTasks.add(createNewMessagingArea);
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
                            //TODO: FRONT END HERE
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
     * Loads all of the chat rooms and any new chats are added to the variable "chatList".
     * This should NOT be used to enter a chat room.
     */
    //TODO: NEEDS TESTING

    public static void LoadAllChatRooms(final ArrayAdapter adapter) {
        if(MainActivity.user.getChatList() == null){
            return;
        }
        adapter.clear();
        final List<String> chatIDLists = MainActivity.user.getChatList();
        System.out.println(chatIDLists);
        for (final String chatID : chatIDLists) {
            FirebaseHelper.messagingDB.getReference("Chats").child(chatID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        ChatInfo chatInfo  = dataSnapshot.getValue(ChatInfo.class);
                        updateChatList(chatID, chatInfo, adapter);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            /*
            FirebaseHelper.messagingDB.getReference("Chats").child(chatID).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if (dataSnapshot.exists()) {
                        Long timeCreated = (Long)dataSnapshot.child("TimeCreated").getValue();
                        String prevMess = (String)dataSnapshot.child("previousMessage").getValue();
                        String title = (String)dataSnapshot.child("title").getValue();

                        System.out.println(timeCreated + " " + prevMess + " " + title);

                        updateChatList(chatID, new ChatInfo(timeCreated,prevMess,title), adapter);
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
            */
        }
    }

    //Creates a map then adds the map into the chatList
    private static void updateChatList(String chatID, ChatInfo chatInfo, ArrayAdapter adapter){
        HashMap<String, ChatInfo> map = new HashMap<>();
        map.put(chatID, chatInfo);
        adapter.add(map);
        chatList.add(map);
}

    //TODO: NEEDS TESTING
    //Loads a single chat room. This should be used only for entering a chat room within the app.
    public static void LoadChatRoom(String chatID) {
        currentChatID = chatID;
        String storagePath = "Messaging Rooms/" + currentChatID + "images/messages/";
        StorageReference reference = FirebaseHelper.mainStorage.getReference().child(storagePath);
        uploadTasks = reference.getActiveUploadTasks();
        FirebaseHelper.messagingDB.getReference("Messages").child(currentChatID).orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    //Creates a new MessageList and copies all the messages received into the new list. This is inefficient but it should work.
                    //TODO: Create a new efficient method of getting a list of messages from Firebase. Try queries
                    currentChatRoom = new MessageList<>();
                    List<MessagingInstance> messagingInstanceList = (List<MessagingInstance>) dataSnapshot.getValue();
                    for (MessagingInstance message: messagingInstanceList) {
                        currentChatRoom.add(message);
                    }
                    //TODO: ADD A MESSAGING SCREEN TO GO TO
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("TAG", "Changed");
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                //TODO: Add remove handler
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //TODO: Add moved handler
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //TODO: Add Toast on cancelled event
            }
        });

    }

    //TODO: NEEDS TESTING
    //Sends a basic text message
    public static void sendTextMessage(String textmessage) {
        MessagingInstance message = MessagingFactory.initializeTextMessagingInstance(textmessage); //Creates a new text message object
        if (currentChatRoom != null) {
            currentChatRoom.offer(message); //Sends message into the messageList
            updatePreviousMessage(textmessage); //Updates the message data into Firebase
        }

    }

    //TODO: NEEDS TESTING
    //Sends an image message from the device's existing images.
    public static void sendImageMessage(String src){

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
                double progress = (100*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                System.out.println("Upload is" + progress + "% done");
                //TODO: FRONTEND display the progress here
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() { //Handles successful upload
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                if(currentChatRoom != null){
                    currentChatRoom.offer(message);
                    updatePreviousMessage(downloadURL);
                }

            }
        });
    }

    //TODO: FINISH METHOD
    //Sends an image message based on Android's ImageView. This should be used to take pictures directly from the app.
    //SHOULD NOT BE USED FOR EXISTING IMAGES.
    public static void sendImageMessage(ImageView image){}


    //Updates "previous message" in the database
    private static void updatePreviousMessage(String message) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("previousMessage", message);
        FirebaseHelper.messagingDB.getReference().child("Chats").child(currentChatID).updateChildren(map);
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
