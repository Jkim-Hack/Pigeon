package com.example.pigeon.common;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.Person;

import com.example.pigeon.Activities.MainActivity;
import com.example.pigeon.Activities.MainMenuActivity;
import com.example.pigeon.Activities.MessagingRoomActivity;
import com.example.pigeon.Activities.StartingActivity;
import com.example.pigeon.FirebaseManagers.Messaging.MessagingHelper;
import com.example.pigeon.R;
import com.example.pigeon.common.UserInfo.ContactInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class NotificationHelper {

    public static HashMap<String, ArrayList<Integer>> chatNotificationIDs = new HashMap<>();
    public static List<Integer> contactRequestNotifIds = new ArrayList<>();
    public static List<Integer> contactMadeNotifIds = new ArrayList<>();
    public static String CHANNEL_ID = "DEFAULT";

    public static void createNotificationChannel(Context context){
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "User";
            String description = "All message and contact notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }

    public static void createNotificationChannel(Context context, String channelID){
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        CHANNEL_ID = channelID;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "User";
            String description = "All message and contact notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }

    //TODO: MAKE ICON
    public static void sendMessagingNotification(String chatID, String text, Person person, long timestamp, Context context) {
        //MessagingHelper.LoadChatRoom(chatID);
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(context, MessagingRoomActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(person.getName())
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.MessagingStyle(person)
                        .addMessage(text, timestamp, person))
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < 9; i++){ //16 is the length of the string
            sb.append(Integer.toString(random.nextInt(10)));
        }
        int notificationId = Integer.parseInt(sb.toString());

        ArrayList<Integer> list = null;
        if(chatNotificationIDs.get(chatID) == null)
            list = new ArrayList<>();
        else
            list = chatNotificationIDs.get(chatID);

        list.add(notificationId);
        chatNotificationIDs.put(chatID, list);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, builder.build());


    }

    public static void sendContactRequestNotification(ContactInfo contactInfo, Context context){
        Intent intent = new Intent(context, MainMenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Pigeon")
                .setContentText(contactInfo.getName() + " requested to be your contact!")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < 9; i++){ //16 is the length of the string
            sb.append(Integer.toString(random.nextInt(10)));
        }
        int notificationId = Integer.parseInt(sb.toString());
        contactRequestNotifIds.add(notificationId);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, builder.build());
    }

    public static void sendContactAcceptedNotification(ContactInfo contactInfo, Context context){
        Intent intent;

        if(MainActivity.user == null){
            intent = new Intent(context, StartingActivity.class);
        }
        else {
            intent = new Intent(context, MainMenuActivity.class);
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Pigeon")
                .setContentText(contactInfo.getName() + " accepted your contact request!")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < 9; i++){ //16 is the length of the string
            sb.append(Integer.toString(random.nextInt(10)));
        }
        int notificationId = Integer.parseInt(sb.toString());
        contactMadeNotifIds.add(notificationId);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, builder.build());
    }


}
