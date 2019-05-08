package com.example.pigeon.FirebaseManagers.Messaging;

import com.example.pigeon.FirebaseManagers.Messaging.MessagingInstance;
import com.example.pigeon.MainActivity;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class MessagingFactory {

    //channelPath = the path in the database where we want to open a channel
    public static MessagingInstance initializeTextMessagingInstance(String message){
        String userID = MainActivity.user.getuID();
        return new TextMessage(message, userID);
    }

    //More will be added:
        //Images, Links, Videos, etc.

}
