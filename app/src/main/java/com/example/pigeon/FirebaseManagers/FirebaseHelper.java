package com.example.pigeon.FirebaseManagers;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHelper {

    public static FirebaseDatabase mainDB;
    public static FirebaseDatabase messagingDB;
    public static FirebaseAuth mainAuth;

    public static void build() {
        mainAuth = FirebaseAuth.getInstance();
        mainDB = FirebaseDatabase.getInstance("https://pigeon-engine.firebaseio.com/");
        messagingDB = FirebaseDatabase.getInstance("https://pigeon-engine-messaging.firebaseio.com/");
    }

}
