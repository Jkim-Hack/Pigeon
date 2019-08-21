package com.example.pigeon.FirebaseManagers.Messaging.Imaging;

import com.example.pigeon.FirebaseManagers.Messaging.MessagingInstance;

public class ImageMessage extends MessagingInstance {

    private String downloadPath;

    public ImageMessage(){
        //TODO: ADD DEFAULT MESSAGE
    }

    public ImageMessage(String downloadPath, String userID){
        this.downloadPath = downloadPath;
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

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }
}
