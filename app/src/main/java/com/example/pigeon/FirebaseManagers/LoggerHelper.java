package com.example.pigeon.FirebaseManagers;

import com.example.pigeon.Activities.MainActivity;
import com.example.pigeon.common.LogEntry;

public class LoggerHelper {

    public static void sendLog(LogEntry logEntry){
        FirebaseHelper.logDB.getReference().child(logEntry.getTag()).push().setValue(logEntry);
        System.out.println(logEntry.getLog());
    }


}
