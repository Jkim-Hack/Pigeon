package com.example.pigeon.FirebaseManagers.Messaging;

public abstract class MessagingInstance {

    protected String dbPath;
    protected String userID;
    protected long sentTimestamp;
    protected String type;

    public abstract String getDBPath();
    public abstract String getUserID();
    public abstract long getSentTimestamp();
    public abstract String getType();

    public abstract void setDBPath(String path);
    public abstract void setUserID(String userID);

}
