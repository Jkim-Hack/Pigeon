package com.example.pigeon.FirebaseManagers.Accounts;


import java.util.UUID;

public class AccountFactory {


    public static AccountInstance getAccountInstance(){

    }

    public static AccountInstance getAccountInstanceFromExisitng(){

    }

    private static String generateNewID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

}
