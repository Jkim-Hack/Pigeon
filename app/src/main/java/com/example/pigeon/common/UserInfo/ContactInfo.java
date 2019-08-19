package com.example.pigeon.common.UserInfo;

import android.support.v4.app.Person;


public class ContactInfo {

    private String image;
    private String name;
    private String chatID;
    private String userID;

    public ContactInfo() {}

    public ContactInfo(String name, String userID) {
        this.name = name;
        this.userID = userID;
    }

    public ContactInfo(String name, String image, String userID) {
        this.name = name;
        this.image = image;
        this.userID = userID;
    }
    public ContactInfo(String name, String chatID, String image, String userID) {
        this.name = name;
        this.chatID = chatID;
        this.image = image;
        this.userID = userID;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Person buildPerson(){
        Person person = new Person.Builder()
                .setKey(userID)
                .setName(name)
                .build();
        return person;
    }

}
