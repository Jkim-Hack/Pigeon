package com.example.pigeon.FirebaseManagers.Accounts;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.pigeon.Activities.Adapters.MessageListAdapter;
import com.example.pigeon.Activities.MainMenuActivity;
import com.example.pigeon.Activities.MessagingRoomActivity;
import com.example.pigeon.FirebaseManagers.FirebaseHelper;
import com.example.pigeon.Activities.MainActivity;
import com.example.pigeon.FirebaseManagers.LoggerHelper;
import com.example.pigeon.FirebaseManagers.Messaging.MessageList;
import com.example.pigeon.FirebaseManagers.Messaging.MessagingHelper;
import com.example.pigeon.FirebaseManagers.Messaging.MessagingInstance;
import com.example.pigeon.common.LogEntry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.logging.Logger;

import static android.content.ContentValues.TAG;
import static com.example.pigeon.FirebaseManagers.Messaging.MessagingHelper.TIMESTAMP;
import static com.example.pigeon.FirebaseManagers.Messaging.MessagingHelper.TITLE;
import static com.example.pigeon.FirebaseManagers.Messaging.MessagingHelper.chatrooms;

public class LoggingInHelper {

    private static Activity signUpActivity;
    private static Activity signInActivity;

    private static AppCompatActivity currentActivity;


    public static void signUpUser(final String email, final String password, final String name, final Activity activity, AppCompatActivity signUpCompactActivity) {
        signUpActivity = activity;
        currentActivity = signUpCompactActivity;
        FirebaseHelper.mainAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser firebaseUser = FirebaseHelper.mainAuth.getCurrentUser();
                            createNewUser(email, name, firebaseUser.getUid());
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(activity, "Sign up failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }


    public static void signUpUser(final String email, final String password, final String name, final long phoneNumber, Activity activity, AppCompatActivity signUpCompactActivity) {
        signUpActivity = activity;
        currentActivity = signUpCompactActivity;
        FirebaseHelper.mainAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser firebaseUser = FirebaseHelper.mainAuth.getCurrentUser();
                            createNewUser(email, name, firebaseUser.getUid(), phoneNumber);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            //Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                            //      Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    public static void signInUser(String email, String password, final Activity activity, AppCompatActivity signInCompactActivity) {
        signInActivity = activity;
        currentActivity = signInCompactActivity;
        FirebaseHelper.mainAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = FirebaseHelper.mainAuth.getCurrentUser();
                            createExistingUser(user.getUid());
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(signInActivity, "Email or password is incorrect.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private static void createNewUser(String email, String name, String uID) {
        HashMap<String, String> userMap = new HashMap<>();
        userMap.put("email", email);
        userMap.put("name", name);
        userMap.put("uID", uID);

        FirebaseHelper.mainDB.getReference().child(FirebaseHelper.CLR).push().setValue(userMap); //Pushes a new create user request into Firebase
        LoggerHelper.sendLog(new LogEntry("New client sign up", "Unknown User"));

        //Listens for a new value called
        FirebaseHelper.mainDB.getReference().child(uID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    //Get the user from firebase and make it the current user
                    User acquiredUser = dataSnapshot.getValue(User.class);
                    MainActivity.user = new User(acquiredUser);
                    System.out.println(MainActivity.user.getClientNum());

                    //Sets up geting new chatrooms
                    setupMessaging(currentActivity.getApplicationContext());

                    //Switch activities
                    Intent intent = new Intent(signUpActivity, MainMenuActivity.class);
                    currentActivity.startActivity(intent);

                    LoggerHelper.sendLog(new LogEntry("Client signed up---ID: " + MainActivity.user.getuID(), MainActivity.user.getClientNum()));

                    //Remove this ValueEventListener
                    FirebaseHelper.mainDB.getReference().child(MainActivity.user.getuID()).removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getDetails());
            }
        });
    }

    private static void setupMessaging(final Context context) {
        FirebaseHelper.mainDB.getReference().child(MainActivity.user.getuID()).child(FirebaseHelper.CHATLIST).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                LoggerHelper.sendLog(new LogEntry("Starting messaging setup...", MainActivity.user.getClientNum()));
                if (dataSnapshot.exists()) {
                    String chatUUID = (String) dataSnapshot.getValue();

                    if (chatrooms.get(chatUUID) == null || chatrooms.get(chatUUID).isEmpty()) {
                        MessageList<MessagingInstance> messageList = new MessageList<>(); //Create a new message list that we populate
                        messageList.addListener(new MessagingHelper.ListListener()); //Add the listener for it
                        chatrooms.put(chatUUID, messageList); //Put it into rhe chatrooms map
                    }

                    if (MessagingHelper.adapters.get(chatUUID) == null || MessagingHelper.adapters.get(chatUUID).isEmpty()) {
                        MessageListAdapter messageListAdapter = new MessageListAdapter(context); //Create a new adapter to put into the map of adapters
                        MessagingHelper.adapters.put(chatUUID, messageListAdapter); //Add the adapter with the chat id as the key
                    }

                    if (MainActivity.user.getChatMap() == null || MainActivity.user.getChatMap().get(chatUUID) == null || MainActivity.user.getChatMap().get(chatUUID).isEmpty()) {
                        MainActivity.user.addChat(dataSnapshot.getKey(), chatUUID); //Add the chat into the User's object
                    }
                    getChatInfo(chatUUID); //Gets the chatinfo of the specified id and adds it to the adapter
                    getChatMembers(chatUUID);

                    LoggerHelper.sendLog(new LogEntry("Messaging setup complete", MainActivity.user.getClientNum())); //Push a log entry to the servlet

                    MessagingHelper.createMessagingListener(chatUUID); //Adds a new messaging listener for the chat
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
                Log.e(ContentValues.TAG, databaseError.getDetails());
                LoggerHelper.sendLog(new LogEntry(databaseError.getDetails(), MainActivity.user.getClientNum()));
            }
        });

    }

    private static void getChatInfo(String id) {
        final String chatUUID = id;

        FirebaseHelper.messagingDB.getReference().child("Chats").child(chatUUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                MessagingHelper.ChatInfo chatInfo = dataSnapshot.getValue(MessagingHelper.ChatInfo.class);
                if (MainMenuActivity.chatListAdapter != null) {
                    HashMap info = new HashMap();
                    info.put(chatUUID, chatInfo);
                    MainMenuActivity.chatListAdapter.add(info);
                    MessagingHelper.chatList.put(chatUUID, chatInfo);
                } else {
                    MessagingHelper.tempChatList.put(chatUUID, chatInfo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private static void getChatMembers(final String chatID) {
        final String chatUUID = chatID;

        FirebaseHelper.messagingDB.getReference().child("Chat Members").child(chatUUID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (MessagingHelper.chatMemebers.get(chatUUID) == null || MessagingHelper.chatMemebers.get(chatUUID).isEmpty()) {
                    HashMap<String, String> member = new HashMap<>();
                    member.put(dataSnapshot.getKey(), dataSnapshot.getValue(String.class));
                    MessagingHelper.chatMemebers.put(chatUUID, member);
                } else {
                    HashMap<String, String> members = MessagingHelper.chatMemebers.get(chatUUID);
                    members.put(dataSnapshot.getKey(), dataSnapshot.getValue(String.class));
                }
                MainMenuActivity.chatListAdapter.notifyDataSetChanged();


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (MessagingHelper.chatMemebers.get(chatUUID) == null || MessagingHelper.chatMemebers.get(chatUUID).isEmpty()) {
                    HashMap<String, String> member = new HashMap<>();
                    member.put(dataSnapshot.getKey(), dataSnapshot.getValue(String.class));
                    MessagingHelper.chatMemebers.put(chatUUID, member);
                } else {
                    HashMap<String, String> members = MessagingHelper.chatMemebers.get(chatUUID);
                    members.remove(dataSnapshot.getKey());
                    members.put(dataSnapshot.getKey(), dataSnapshot.getValue(String.class));
                }
                MainMenuActivity.chatListAdapter.notifyDataSetChanged();


            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                if (MessagingHelper.chatMemebers.get(chatUUID) == null || MessagingHelper.chatMemebers.get(chatUUID).isEmpty()) {
                    HashMap<String, String> members = MessagingHelper.chatMemebers.get(chatUUID);
                    members.remove(dataSnapshot.getKey());
                }
                MainMenuActivity.chatListAdapter.notifyDataSetChanged();


            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //Not used
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getDetails());
            }
        });
    }


    private static void createNewUser(String email, String name, String uID, long phonenumber) {
        MainActivity.user = new User(email, name, uID, phonenumber);
        FirebaseHelper.mainDB.getReference().child(uID).setValue(MainActivity.user, new OnUserComplete());
    }

    private static void createExistingUser(String uID) {
        //TODO: FIX SIGN IN ERROR WHERE dataSnapshot.getValue() DOESN'T GIVE USER CLASS BUT GIVES A HASHMAP.
        //Creates a new listener
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //HashMap data = (HashMap)dataSnapshot.getValue();

                //User user = new User((String)data.get("email"), (String)data.get("name"), (String)data.get("uID"), (Long)data.get("phonenumber"));
                // user.setClientNum((String)data.get("clientNum"));

                User user = (User) dataSnapshot.getValue(User.class);

                MainActivity.user = new User(user);
                LoggerHelper.sendLog(new LogEntry("User signed in", MainActivity.user.getClientNum()));
                setupMessaging(currentActivity.getApplicationContext());
                Intent intent = new Intent(signInActivity, MainMenuActivity.class);
                currentActivity.startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "errorAccessingUser");
                Log.e(TAG, databaseError.getDetails());
            }
        };
        FirebaseHelper.mainDB.getReference().child(uID).addListenerForSingleValueEvent(listener);
    }


    static class OnUserComplete implements DatabaseReference.CompletionListener {
        @Override
        public void onComplete(DatabaseError error, DatabaseReference ref) {
            if (error == null) {
                Log.w(TAG, "userInDatabase:success");
                Intent intent = new Intent(signUpActivity, MainMenuActivity.class);
                currentActivity.startActivity(intent);
                //Update UI here
            } else {
                Log.w(TAG, "userInDatabase:FAILED");
                //Update UI here
            }

        }
    }


}
