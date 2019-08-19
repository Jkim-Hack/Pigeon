package com.example.pigeon.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.pigeon.Services.ChatNotificationService;
import com.example.pigeon.Services.ContactsNotificationService;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            System.out.println("KDSLKDSLDKLSKDLSKDLSDKSLDKLSDKL");
            Intent chatServiceIntent = new Intent(context, ChatNotificationService.class);
            context.startService(chatServiceIntent);
            Intent contactServiceIntent = new Intent(context, ContactsNotificationService.class);
            context.startService(contactServiceIntent);
        }
    }
}
