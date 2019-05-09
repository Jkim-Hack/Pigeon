package com.example.pigeon.FirebaseManagers.Messaging;

public abstract class MessagingInstance {

    protected String dbPath;
    protected String userID;
    protected long sentTimestamp;

    public abstract String getDBPath();
    public abstract String getUserID();
    public abstract long getSentTimestamp();

    public abstract String setDBPath();
    public abstract String setUserID();
    public abstract long setSentTimestamp();

}
