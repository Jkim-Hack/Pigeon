package com.example.pigeon.FirebaseManagers.Messaging;

public class TextMessage extends MessagingInstance{
    private String message;

    public TextMessage(){}
    public TextMessage(String message, String userkey){
        this.message = message;
        this.sentTimestamp = System.currentTimeMillis();
        this.userID = userkey;
    }

    @Override
    public String getDBPath() { return dbPath; }
    @Override
    public String getUserID() {
        return userID;
    }
    @Override
    public long getSentTimestamp() {
        return sentTimestamp;
    }

    @Override
    public String setDBPath() { return dbPath; }
    @Override
    public String setUserID() {
        return userID;
    }
    @Override
    public long setSentTimestamp() {
        return sentTimestamp;
    }
}
