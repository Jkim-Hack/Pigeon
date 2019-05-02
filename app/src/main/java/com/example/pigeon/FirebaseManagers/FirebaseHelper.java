package com.example.pigeon.FirebaseManagers;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHelper {
    private FirebaseApp mainApp;
    public FirebaseDatabase mainDB;
    public FirebaseDatabase messagingDB;

    public void build() {
        mainApp = mainDB.getApp();
        mainDB = FirebaseDatabase.getInstance(mainApp, "https://pigeon-engine.firebaseio.com/");
        messagingDB = FirebaseDatabase.getInstance(mainApp, "https://pigeon-engine-messaging.firebaseio.com/");
    }

}
