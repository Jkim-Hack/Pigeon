package com.example.pigeon.FirebaseManagers.Messaging.Text;

import com.example.pigeon.FirebaseManagers.Messaging.MessagingInstance;

public class TextMessage extends MessagingInstance {
    private String message;

    public TextMessage(){
        //TODO: ADD DEFAULT MESSAGE
    }
    public TextMessage(String message, String userID){
        this.message = message;
        this.sentTimestamp = System.currentTimeMillis();
        this.userID = userID;
        this.type = "TEXT";
    }
    public TextMessage(String message, String userID, long timestamp){
        this.message = message;
        this.sentTimestamp = timestamp;
        this.userID = userID;
        this.type = "TEXT";
    }


    public String getMessage(){return message;}
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
    public void setUserID(String userID) {
        this.userID = userID;
    }
}
