package com.example.pigeon.Activities;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.pigeon.Activities.Adapters.ChatListAdapter;
import com.example.pigeon.Activities.Adapters.MessageListAdapter;
import com.example.pigeon.FirebaseManagers.Messaging.MessagingHelper;
import com.example.pigeon.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainMenuActivity extends AppCompatActivity {


    public static ChatListAdapter chatListAdapter;

    private Activity getActivity() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        //Creates the new ListView object and adapter object
        final ListView chatList = findViewById(R.id.chatList);
        chatListAdapter = new ChatListAdapter(this, R.layout.chat_menu_item);
        MessagingHelper.LoadAllChatRooms(chatListAdapter); //Loads all chat rooms
        //TODO: Need to update whenever a new chatroom loads.

        chatList.setAdapter(chatListAdapter); //Add the adapter to the list view

        chatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, MessagingHelper.ChatInfo> item =
                        (HashMap<String, MessagingHelper.ChatInfo>)chatList.getItemAtPosition(position);
                Set<Map.Entry<String, MessagingHelper.ChatInfo>> entries = item.entrySet();
                System.out.println(entries);
                if(entries != null) {
                    //Iterate through
                    Iterator<Map.Entry<String, MessagingHelper.ChatInfo>> iterator = entries.iterator();
                    Map.Entry<String, MessagingHelper.ChatInfo> entry = null;
                    while (iterator.hasNext()) {
                        entry = iterator.next(); //Sets entry as the map value for our chat info
                    }
                    String chatID = entry.getKey();
                    System.out.println(chatID);
                    MessagingHelper.LoadChatRoom(chatID, getActivity(), view.getContext());
                }
            }
        });

        Button createButton = findViewById(R.id.createChatButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Task<Void>> pool = MessagingHelper.createChat("GZ8DGKZWIoYuUoSDs5XFC63jeiO2", view.getContext());
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
                                //MessagingHelper.LoadAllChatRooms(chatListAdapter); //Loads all chat rooms
                                System.out.println("READY");
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
