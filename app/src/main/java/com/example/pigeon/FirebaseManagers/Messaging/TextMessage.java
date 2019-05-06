package com.example.pigeon.FirebaseManagers.Messaging;

public class TextMessage extends MessagingInstance{
    private String message;

    public TextMessage(){}
    public TextMessage(String message, long timestamp, String userkey){
        this.message = message;
        this.sentTimestamp = timestamp;
        this.userID = userkey;
    }

    @Override
    public String getDBPath() {
        return dbPath;
    }
    @Override
    public String getUserID() {
        return userID;
    }
    @Override
    public long getSentTimestamp() {
        return sentTimestamp;
    }

}
