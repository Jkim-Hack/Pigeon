package com.example.pigeon.Activities;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.pigeon.Activities.Adapters.ChatListAdapter;
import com.example.pigeon.FirebaseManagers.Messaging.MessagingHelper;
import com.example.pigeon.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class MainMenuActivity extends AppCompatActivity {


    public static ChatListAdapter chatListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        //Creates the new ListView object and adapter object
        ListView chatList = findViewById(R.id.chatList);
        chatListAdapter = new ChatListAdapter(this, R.layout.chat_menu_item);
        MessagingHelper.LoadAllChatRooms(chatListAdapter); //Loads all chat rooms
        //TODO: Need to update whenever a new chatroom loads.

        chatList.setAdapter(chatListAdapter); //Add the adapter to the list view

        Button createButton = findViewById(R.id.createChatButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Task<Void>> pool = MessagingHelper.createChat("9lWzsoVkeNZAmX2fwleOdpKkMF63");
                for(int i = 0; i < pool.size(); i++){
                    if(i != pool.size()-1){
                        pool.get(i).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                System.out.println("finished task");
                            }
                        });
                    } else {
                        pool.get(i).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                MessagingHelper.LoadAllChatRooms(chatListAdapter); //Loads all chat rooms
                            }
                        });
                    }
                    pool.get(i).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println("FAILED");
                        }
                    });
                }
            }
        });


    }
}
