package com.example.pigeon.FirebaseManagers.Accounts;


import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.pigeon.FirebaseManagers.FirebaseHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.UUID;

import static android.content.ContentValues.TAG;

public class AccountFactory {


    public static AccountInstance getAccountInstance(String email, String password, String name, long phonenumber){
        DatabaseReference database = FirebaseHelper.mainDB.getReference();
        String uniqueID = generateNewID(); //Generates a new UUID but we may need to switch to firebase's
        FirebaseHelper.mainAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = FirebaseHelper.mainAuth.getCurrentUser();
                            updateUID(user.getUid());
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            //Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                              //      Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }

    public static AccountInstance getAccountInstanceFromExisitng(){

    }

    private static String generateNewID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    private static void updateUID(String uid) {

    }

}
