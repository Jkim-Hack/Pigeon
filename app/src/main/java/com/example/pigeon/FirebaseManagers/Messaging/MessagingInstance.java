package com.example.pigeon.FirebaseManagers.Messaging;

public abstract class MessagingInstance {

    protected String userID;
    protected long sentTimestamp;
    protected String type;

    public abstract String getUserID();
    public abstract long getSentTimestamp();
    public abstract String getType();

    public abstract void setUserID(String userID);

}
