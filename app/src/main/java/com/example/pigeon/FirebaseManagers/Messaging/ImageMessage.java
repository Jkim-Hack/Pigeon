package com.example.pigeon.FirebaseManagers.Messaging;

public class ImageMessage extends MessagingInstance {

    private String downloadLink;

    public ImageMessage(){
        //TODO: ADD DEFAULT MESSAGE
    }

    public ImageMessage(String downloadLink, String userID){
        this.downloadLink = downloadLink;
        this.sentTimestamp = System.currentTimeMillis();
        this.userID = userID;
        this.type = "IMAGE";
    }

    @Override
    public String getUserID() {
        return this.userID;
    }

    @Override
    public long getSentTimestamp() {
        return this.sentTimestamp;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public void setUserID(String userID) {
        this.userID = userID;
    }
}
