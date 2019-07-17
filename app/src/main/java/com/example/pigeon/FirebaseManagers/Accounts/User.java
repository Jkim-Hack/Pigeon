package com.example.pigeon.FirebaseManagers.Accounts;

import android.support.annotation.NonNull;

import com.example.pigeon.Activities.MainMenuActivity;
import com.example.pigeon.FirebaseManagers.FirebaseHelper;
import com.example.pigeon.FirebaseManagers.Messaging.MessagingHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class User{

    private String email;
    private String name;
    private String uID;
    private String clientNum;
    private long phonenumber;
    private List<String> chatList;

    public User() {
        this.email = "user@gmail.com";
        this.name = "John Doe";
    }

    public User(String email, String name, String uID, long phonenumber){
        this.email = email;
        this.name = name;
        this.uID = uID;
        this.phonenumber = phonenumber;
        this.chatList = new ArrayList<>();
        this.chatList.add("test");
    }

    public User(String email, String name, String uID){
        this.email = email;
        this.name = name;
        this.uID = uID;
        this.phonenumber = 0;
        this.chatList = new ArrayList<>();
        this.chatList.add("test");
    }

    public User(User otherUser){
        this.email = otherUser.getEmail();
        this.name = otherUser.getName();
        this.uID = otherUser.getuID();
        this.phonenumber = otherUser.getPhonenumber();
        this.chatList = otherUser.getChatList();
        this.clientNum = otherUser.clientNum;
    }

    public void updateUser(String email, String name, String uID, long phonenumber) {
        this.email = email;
        this.name = name;
        this.uID = uID;
        this.phonenumber = phonenumber;
    }



    public String getEmail() {
        return email;
    }

    public String getuID() {
        return uID;
    }

    public String getName() {
        return name;
    }

    public String getClientNum() { return clientNum; }

    public void setClientNum(String clientNum) { this.clientNum = clientNum; }

    public long getPhonenumber() {
        return phonenumber;
    }

    public List<String> getChatList() {
        return chatList;
    }

    public void addChat(String chatID) {
        if(chatList == null){
            chatList = new ArrayList<>();
        }
        System.out.println("try");
        chatList.add(chatID);
        //ChatList add should be done after the user has been updated
        Task<Void> addChat = FirebaseHelper.mainDB.getReference().child(this.uID).child("chatList").setValue(chatList);
        addChat.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                System.out.println("added");
                MessagingHelper.LoadAllChatRooms(MainMenuActivity.chatListAdapter);
            }
        });
        addChat.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("FAILED");
                System.out.println(e.getMessage());
            }
        });


        chatList.add(chatID);


    }

    @Override
    public String toString() {
        return "Email:" + email + " Name:" + name + " uID:" + uID + " Phone Number:" + phonenumber + "Chars: " + chatList;
    }
}
