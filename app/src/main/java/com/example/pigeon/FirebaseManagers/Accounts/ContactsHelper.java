package com.example.pigeon.FirebaseManagers.Accounts;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.pigeon.Activities.Adapters.Contacts.ContactRequestsAdapter;
import com.example.pigeon.Activities.Fragments.Contacts.ContactsFragment;
import com.example.pigeon.Activities.MainActivity;
import com.example.pigeon.FirebaseManagers.FirebaseHelper;
import com.example.pigeon.FirebaseManagers.LoggerHelper;
import com.example.pigeon.common.ContactInfo;
import com.example.pigeon.common.LogEntry;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;
import static com.example.pigeon.Activities.MainMenuActivity.contactRequestsAdapter;
import static com.example.pigeon.Activities.MainMenuActivity.contactsListAdapter;
import static com.example.pigeon.Activities.MainMenuActivity.tempContactsList;


public class ContactsHelper {

    public static final Integer REQUESTUSER = 3;
    public static final String USER = "user";
    public static final String CONTACTSLIST = "ContactsList";
    private static final String CONTACTREQUESTS = "ContactRequests";

    public static HashMap<String, ContactInfo> contacts;
    public static HashMap<String, ContactInfo> contactRequests;

    public static void requestContact(String uid) {
        if (contacts == null)
            contacts = new HashMap<>(); //Create new list because the first instance will have a null list
        if(contactRequests == null)
            contactRequests = new HashMap<>();

        final String userID = uid;
        long time = System.currentTimeMillis();

        //Setup command
        HashMap command = new HashMap();
        command.put(FirebaseHelper.COMMAND, REQUESTUSER);
        command.put(USER, userID);
        command.put(FirebaseHelper.TIMESTAMP, time);

        LoggerHelper.sendLog(new LogEntry("Requesting new contact " + userID + "...", MainActivity.user.getClientNum())); //Pushes log to server

        //Push the command into Firebase
        final Query commandI = FirebaseHelper.mainDB.getReference(FirebaseHelper.commandInbox).child(MainActivity.user.getClientNum()).push();
        ((DatabaseReference) commandI).setValue(command);

        //We need to know when the command Query is done, we need to get the value of the DataSnapshot, the key, and finally listen for that key value within the user's contact list.
        commandI.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String key = dataSnapshot.getKey();
                FirebaseHelper.mainDB.getReference().child(CONTACTSLIST).child(MainActivity.user.getuID()).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if (dataSnapshot.exists()) {
                            ContactInfo userInfo = dataSnapshot.getValue(ContactInfo.class); //Gets the ContactInfo
                            if (dataSnapshot.getKey().equals(key)) { //Identifies the key associated
                                //TODO: ADD NOTIFICATION THAT THE USER ACCEPTED REQUEST
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

    public static void LoadContacts() {
        if (contacts == null)
            contacts = new HashMap<>();
        if(contactRequests == null)
            contactRequests = new HashMap<>();

        final String userID = MainActivity.user.getuID();

        FirebaseHelper.mainDB.getReference().child(CONTACTSLIST).child(userID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ContactInfo contactInfo = dataSnapshot.getValue(ContactInfo.class);
                String key = dataSnapshot.getKey();
                if(!contacts.keySet().contains(key)) {
                    contacts.put(key,contactInfo);
                    if(contactsListAdapter == null){
                        tempContactsList.add(contactInfo);
                    } else {
                        contactsListAdapter.put(key, contactInfo);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ContactInfo contactInfo = dataSnapshot.getValue(ContactInfo.class);
                contacts.replace(dataSnapshot.getKey(), contactInfo);
                if(contactsListAdapter != null){
                    contactsListAdapter.put(dataSnapshot.getKey(), contactInfo);
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                contacts.remove(dataSnapshot.getKey());
                if(contactsListAdapter != null){
                    contactsListAdapter.remove(dataSnapshot.getKey());
                }
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

        FirebaseHelper.mainDB.getReference().child(CONTACTREQUESTS).child(MainActivity.user.getuID()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                contactRequests.put(dataSnapshot.getKey(), dataSnapshot.getValue(ContactInfo.class));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                contactRequests.replace(dataSnapshot.getKey(), dataSnapshot.getValue(ContactInfo.class));
                contactRequestsAdapter.replace(dataSnapshot.getKey(), dataSnapshot.getValue(ContactInfo.class));
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

    public static void acceptContact(ContactInfo contactInfo, String key) {
        FirebaseHelper.mainDB.getReference().child(CONTACTSLIST).child(MainActivity.user.getuID()).child(key).setValue(contactInfo);
        FirebaseHelper.mainDB.getReference().child(CONTACTREQUESTS).child(MainActivity.user.getuID()).child(key).removeValue();
        contactRequestsAdapter.remove(key);
        contactsListAdapter.notifyDataSetChanged();
    }

    public static void ignoreContact(ContactInfo contactInfo, String key) {
        contactRequestsAdapter.remove(key);
        FirebaseHelper.mainDB.getReference().child(CONTACTREQUESTS).child(MainActivity.user.getuID()).child(key).removeValue();
        FirebaseHelper.mainDB.getReference().child(CONTACTREQUESTS).child(key).removeValue();
    }

    public static void deleteContact(String key){
        FirebaseHelper.mainDB.getReference().child(CONTACTSLIST).child(MainActivity.user.getuID()).child(key).removeValue();
    }

}
