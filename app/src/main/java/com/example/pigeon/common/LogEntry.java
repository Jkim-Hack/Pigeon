package com.example.pigeon.common;

import com.google.firebase.database.ServerValue;

import java.util.Map;

public class LogEntry {

    private String log;
    private String tag;
    private Long time;


    public LogEntry(String log, String tag){
        this.log = log;
        this.tag = tag;
        this.time = System.currentTimeMillis();
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Map<String, String> getTime(){
        return ServerValue.TIMESTAMP;
    }

    public Long getTimeLong() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

}
