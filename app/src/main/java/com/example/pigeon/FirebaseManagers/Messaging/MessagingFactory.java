package com.example.pigeon.FirebaseManagers.Messaging;

import com.example.pigeon.FirebaseManagers.Messaging.MessagingInstance;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class MessagingFactory {

    //channelPath = the path in the database where we want to open a channel
    public static MessagingInstance initializeMessagingInstance(String channelPath){

        MessagingInstance messagingInstance = new MessagingInstance(channelPath);

        return messagingInstance;
    }

    private static String generateNewID() throws NoSuchAlgorithmException, NoSuchProviderException {

        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", "SUN");



        return null;
    }
}
