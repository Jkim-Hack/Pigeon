package com.example.pigeon.Activities;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.pigeon.Activities.Adapters.ChatListAdapter;
import com.example.pigeon.Activities.Adapters.MessageListAdapter;
import com.example.pigeon.FirebaseManagers.FirebaseHelper;
import com.example.pigeon.FirebaseManagers.Messaging.MessagingHelper;
import com.example.pigeon.FirebaseManagers.Messaging.MessagingInstance;
import com.example.pigeon.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainMenuActivity extends AppCompatActivity {


    public static ChatListAdapter chatListAdapter; //Chat list adapter is what fills out the info in the ListView of chats

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

        chatList.setAdapter(chatListAdapter); //Add the adapter to the list view

        //Loads the chat selected
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
                    MessagingHelper.LoadChatRoom(chatID, getActivity(), view.getContext()); //Loads the chatroom
                }
            }
        });

        //Creates a new chat with the specified uid
        Button createButton = findViewById(R.id.createChatButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> list = new ArrayList<>();
                list.add(MainActivity.user.getuID());
                list.add("AIbFBafwzheXXBdjGh4fp8HnFO12");
                MessagingHelper.createChat(list, view.getContext());
            }
        });



    }
}
