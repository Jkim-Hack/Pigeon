package com.example.pigeon.FirebaseManagers.Messaging;

public class TextMessage extends MessagingInstance{
    private String message;

    public TextMessage(){}
    public TextMessage(String message, String userkey){
        this.message = message;
        this.sentTimestamp = System.currentTimeMillis();
        this.userID = userkey;
        this.type = "TEXT";
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
    public String getType() {return type;}

    @Override
    public void setDBPath(String path) {
        this.dbPath = path;
    }
    @Override
    public void setUserID(String userID) {
        this.userID = userID;
    }
}
