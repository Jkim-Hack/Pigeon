package com.example.pigeon.FirebaseManagers.Accounts;

public interface AccountInstance {

    void remove();
    void signin();
    void signout();
    void signup(String email, String password, String name);

}
