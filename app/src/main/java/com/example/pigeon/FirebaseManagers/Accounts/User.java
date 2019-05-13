package com.example.pigeon.FirebaseManagers.Accounts;

import com.example.pigeon.FirebaseManagers.FirebaseHelper;

import java.util.ArrayList;
import java.util.List;

public class User implements Cloneable{

    private String email;
    private String name;
    private String uID;
    private long phonenumber;
    private ArrayList<String> chatList;

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
    }

    public User(String email, String name, String uID){
        this.email = email;
        this.name = name;
        this.uID = uID;
        this.phonenumber = 0;
        this.chatList = new ArrayList<>();
    }

    public User(User otherUser){
        this.email = otherUser.getEmail();
        this.name = otherUser.getName();
        this.uID = otherUser.getuID();
        this.phonenumber = otherUser.getPhonenumber();
        this.chatList = otherUser.getChatList();
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

    public long getPhonenumber() {
        return phonenumber;
    }

    public ArrayList<String> getChatList() {
        return chatList;
    }

    //WORK IN PROGRESS
    public void addChat(String chatID) {
        if(chatList == null){
            chatList = new ArrayList<>();
        }
        chatList.add(chatID);
        //FirebaseHelper.mainDB.getReference().child(this.uID).child("chatList").setValue(chatList);
    }

    @Override
    public String toString() {
        return "Email:" + email + " Name:" + name + " uID:" + uID + " Phone Number:" + phonenumber + "Chars: " + chatList;
    }
}
