package com.example.pigeon.common.UserInfo;

import java.util.HashMap;

public class User{

    private String email;
    private String name;
    private String uID;
    private String clientNum;
    private long phonenumber;
    private HashMap<String, String> chatMap;

    public User() {
        this.email = "user@gmail.com";
        this.name = "John Doe";
    }

    public User(String email, String name, String uID, long phonenumber){
        this.email = email;
        this.name = name;
        this.uID = uID;
        this.phonenumber = phonenumber;
        this.chatMap = new HashMap<>();
    }

    public User(String email, String name, String uID){
        this.email = email;
        this.name = name;
        this.uID = uID;
        this.phonenumber = 0;
        this.chatMap = new HashMap<>();
    }

    public User(User otherUser){
        this.email = otherUser.getEmail();
        this.name = otherUser.getName();
        this.uID = otherUser.getuID();
        this.phonenumber = otherUser.getPhonenumber();
        this.chatMap = otherUser.getChatMap();
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

    public HashMap<String, String> getChatMap() {
        return chatMap;
    }

    public void addChat(String key, String chatID) {
        if(this.chatMap == null){
            this.chatMap = new HashMap<>();
        }
        this.chatMap.put(key,chatID);
    }

    @Override
    public String toString() {
        return "Email:" + email + " Name:" + name + " uID:" + uID + " Phone Number:" + phonenumber + "Chats: " + this.chatMap.toString();
    }
}
