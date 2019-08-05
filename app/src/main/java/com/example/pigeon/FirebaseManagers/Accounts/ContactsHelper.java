package com.example.pigeon.FirebaseManagers.Accounts;

import android.content.ContentValues;
import android.content.Context;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.pigeon.Activities.Fragments.ContactsFragment;
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


public class ContactsHelper {

    public static final Integer REQUESTUSER = 3;
    public static final String USER = "user";
    public static final String CONTACTSLIST = "ContactsList";
    public static List<ContactInfo> contacts;

    public static void createContact(String uid) {
        if (contacts == null)
            contacts = new ArrayList<>(); //Create new list because the first instance will have a null list
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
                            ContactInfo userInfo = (ContactInfo) dataSnapshot.getValue(); //Gets the ContactInfo
                            if (dataSnapshot.getKey().equals(key)) { //Identifies the key associated
                                contacts.add(userInfo); //Adds into the list of contacts client side
                                ContactsFragment.contactsListAdapter.add(userInfo); //Adds to the adapter list
                                ContactsFragment.contactsListAdapter.notifyDataSetChanged();
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
            contacts = new ArrayList<>();

        final String userID = MainActivity.user.getuID();

        FirebaseHelper.mainDB.getReference().child(CONTACTSLIST).child(userID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ContactInfo contactInfo = dataSnapshot.getValue(ContactInfo.class);
                if(!contacts.contains(contactInfo)) {
                    contacts.add(contactInfo);
                    ContactsFragment.contactsListAdapter.add(contactInfo);
                    ContactsFragment.contactsListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ContactInfo contactInfo = dataSnapshot.getValue(ContactInfo.class);
                String uid = contactInfo.getUserID();
                for(int i = 0; i < contacts.size(); i++){
                    if(contacts.get(i).getUserID().equals(uid)){
                        ContactInfo currInfo = contacts.get(i);
                        contacts.remove(i);
                        ContactsFragment.contactsListAdapter.remove(currInfo);

                        contacts.add(i, contactInfo);
                        ContactsFragment.contactsListAdapter.add(contactInfo);
                        ContactsFragment.contactsListAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                ContactInfo contactInfo = dataSnapshot.getValue(ContactInfo.class);
                contacts.remove(contactInfo);
                ContactsFragment.contactsListAdapter.remove(contactInfo);
                ContactsFragment.contactsListAdapter.notifyDataSetChanged();
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
