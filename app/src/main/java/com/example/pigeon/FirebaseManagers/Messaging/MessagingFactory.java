package com.example.pigeon.FirebaseManagers.Messaging;

import android.widget.ImageView;

import com.example.pigeon.Activities.MainActivity;

public class MessagingFactory {

    //channelPath = the path in the database where we want to open a channel
    public static MessagingInstance initializeTextMessagingInstance(String message){
        String userID = MainActivity.user.getuID();
        return new TextMessage(message, userID);
    }

    public static MessagingInstance initializeImageMessagingInstance(String path){
        String userID = MainActivity.user.getuID();
        return new ImageMessage(path, userID);
    }

    public static MessagingInstance initializeImageMessagingInstance(ImageView image){
        String userID = MainActivity.user.getuID();
        return new ImageMessage(image, userID);
    }

    //More will be added:
        //Images, Links, Videos, etc.

}
