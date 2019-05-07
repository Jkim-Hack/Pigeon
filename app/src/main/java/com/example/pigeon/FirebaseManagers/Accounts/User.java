package com.example.pigeon.FirebaseManagers.Accounts;

public class User implements Cloneable{

    private String email;
    private String name;
    private String uID;
    private long phonenumber;

    public User() {
        this.email = "user@gmail.com";
        this.name = "John Doe";
    }

    public User(String email, String name, String uID, long phonenumber){
        this.email = email;
        this.name = name;
        this.uID = uID;
        this.phonenumber = phonenumber;
    }

    public User(User otherUser){
        this.email = otherUser.getEmail();
        this.name = otherUser.getName();
        this.uID = otherUser.getuID();
        this.phonenumber = otherUser.getPhonenumber();
    }

    public void updateUser(String email, String name, String uID, long phonenumber) {
        this.email = email;
        this.name = name;
        this.uID = uID;
        this.phonenumber = phonenumber;
    }



    public String getEmail() {
        return email;
    }

    public String getuID() {
        return uID;
    }

    public String getName() {
        return name;
    }

    public long getPhonenumber() {
        return phonenumber;
    }

    @Override
    public String toString() {
        return "Email:" + email + " Name:" + name + " uID:" + uID + " Phone Number:" + phonenumber;
    }
}
