package com.example.pigeon.FirebaseManagers.Messaging;

import com.example.pigeon.FirebaseManagers.FirebaseHelper;

public class MessagingHelper {

    public static MessageList currentChatRoom;
    public static String currentChatID;


    //TODO: NEEDS TO RETURN A FUTURE SINCE PUSHING INTO FIREBASE WILL TAKE A BIT OF TIME
    public static void build(String chatID){
        currentChatRoom = new MessageList();
        currentChatID = chatID;
    }


    public static void sendTextMessage(String textmessage){
        MessagingInstance message = MessagingFactory.initializeTextMessagingInstance(textmessage);
        currentChatRoom.offer(message);

    }


    class ListListener implements MessageListListener{
        @Override
        public void OnMessageAdd() {
            //peek just looks at the top object and doesn't remove anything
            FirebaseHelper.messagingDB.getReference().child("Messages").child(currentChatID)
                    .push().setValue(currentChatRoom.peek());
        }
    }

}
