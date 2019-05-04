package com.example.pigeon.FirebaseManagers.Accounts;

public class User {

    private String email;
    private String name;
    private String uID;

    public User() {
        this.email = "user@gmail.com";
        this.name = "John Doe";
    }

    public User(String email, String name, String uID){
        this.email = email;
        this.name = name;
        this.uID = uID;
    }

    public void updateUser(String email, String name, String uID) {
        this.email = email;
        this.name = name;
        this.uID = uID;
    }

}
