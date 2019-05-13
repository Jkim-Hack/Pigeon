package com.example.pigeon.FirebaseManagers.Accounts;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.pigeon.FirebaseManagers.FirebaseHelper;
import com.example.pigeon.Activities.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;

public class LoggingInHelper {

    private static Activity signUpActivity;
    private static Activity signInActivity;

    private static AppCompatActivity currentActivity;

    public static void signUpUser(final String email, final String password, final String name, final Activity activity, AppCompatActivity signUpCompactActivity){
        signUpActivity = activity;
        currentActivity = signUpCompactActivity;
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
                            Toast.makeText(activity, "Sign up failed.",
                                  Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }


    public static void signUpUser(final String email, final String password, final String name, final long phoneNumber, Activity activity,  AppCompatActivity signUpCompactActivity){
        signUpActivity = activity;
        currentActivity = signUpCompactActivity;
        FirebaseHelper.mainAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser firebaseUser = FirebaseHelper.mainAuth.getCurrentUser();
                            createNewUser(email, name, firebaseUser.getUid(), phoneNumber);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            //Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                            //      Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    public static void signInUser(String email, String password, final Activity activity, AppCompatActivity signInCompactActivity){
        signInActivity = activity;
        currentActivity = signInCompactActivity;
        FirebaseHelper.mainAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = FirebaseHelper.mainAuth.getCurrentUser();
                            createExistingUser(user.getUid());
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(signInActivity, "Email or password is incorrect.",
                                 Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private static void createNewUser(String email, String name, String uID) {
        MainActivity.user = new User(email,name, uID);
        FirebaseHelper.mainDB.getReference().child(uID).setValue(MainActivity.user, new OnUserComplete());
    }

    private static void createNewUser(String email, String name, String uID, long phonenumber) {
        MainActivity.user = new User(email,name, uID, phonenumber);
        FirebaseHelper.mainDB.getReference().child(uID).setValue(MainActivity.user, new OnUserComplete());
    }

    private static void createExistingUser(String uID){

        //Creates a new listener
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                MainActivity.user = new User(user);
                Intent intent = new Intent(signInActivity, MainActivity.class);
                currentActivity.startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG , "errorAccessingUser") ;
            }
        };
        FirebaseHelper.mainDB.getReference().child(uID).addListenerForSingleValueEvent(listener);
    }



   static class OnUserComplete implements DatabaseReference.CompletionListener {
        @Override
        public void onComplete(DatabaseError error, DatabaseReference ref) {
            if(error == null){
                Log.w(TAG, "userInDatabase:success");
                Intent intent = new Intent(signUpActivity, MainActivity.class);
                currentActivity.startActivity(intent);
                //Update UI here
            } else {
                Log.w(TAG, "userInDatabase:FAILED");
                //Update UI here
            }

        }
    }






}
