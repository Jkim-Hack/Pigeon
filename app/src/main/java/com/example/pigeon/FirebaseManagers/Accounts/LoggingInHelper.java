package com.example.pigeon.FirebaseManagers.Accounts;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.pigeon.FirebaseManagers.FirebaseHelper;
import com.example.pigeon.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import static android.content.ContentValues.TAG;

public class LoggingInHelper {



    public void signUpUser(final String email, final String password, final String name, final long phoneNumber){
        FirebaseHelper.mainAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser firebaseUser = FirebaseHelper.mainAuth.getCurrentUser();
                            createNewUser(email, name, firebaseUser.getUid());
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            //Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                            //      Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    public void signInUser(String email, String password){
        FirebaseHelper.mainAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = FirebaseHelper.mainAuth.getCurrentUser();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:success", task.getException());
                          //  Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                            //      Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    public void createNewUser(String email, String name, String uID) {
        MainActivity.user = new User(email,name, uID);
        FirebaseHelper.mainDB.getReference().child(uID).setValue(MainActivity.user, new OnUserComplete());
    }

    class OnUserComplete implements DatabaseReference.CompletionListener {
        @Override
        public void onComplete(DatabaseError error, DatabaseReference ref) {
            if(error.getCode() == 0){
                Log.w(TAG, "userInDatabase:success");
            }
        }
    }

}
