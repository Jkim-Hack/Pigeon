package com.example.pigeon.FirebaseManagers;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseHelper {

    public static FirebaseDatabase mainDB;
    public static FirebaseDatabase messagingDB;
    public static FirebaseAuth mainAuth;
    public static FirebaseStorage mainStorage;


    public static final String ACCOUNTS = "Accounts";
    public static final String MESSAGES = "Messages";

    public static final String CLR = "ClientRequests";
    public static final String commandInbox = "CommandInbox";


    public static final String COMMAND = "command";

    public static final int UPDATEPROFILE = 0;
    public static final int CREATECHAT = 1;
    public static final int DELETECHAT = 2;

    public static final String CHATUSERS = "chatUsers";
    public static final String CHATLIST = "chatList";
    public static final String CHATID = "chatID";
    public static final String CLIENTNUM = "clientNum";


    public static void build() {
        mainAuth = FirebaseAuth.getInstance();
        mainDB = FirebaseDatabase.getInstance("https://pigeon-engine.firebaseio.com/");
        messagingDB = FirebaseDatabase.getInstance("https://pigeon-engine-messaging.firebaseio.com/");
        mainStorage = FirebaseStorage.getInstance();
    }

}
