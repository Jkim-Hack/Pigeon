package com.example.pigeon.FirebaseManagers.Messaging;

public class Message {
    private String message;
    private long timestamp;
    private String userkey;

    public Message(){}
    public Message(String message, long timestamp, String userkey){
        this.message = message;
        this.timestamp = timestamp;
        this.userkey = userkey;
    }

}
