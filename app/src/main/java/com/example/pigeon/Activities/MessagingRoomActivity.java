package com.example.pigeon.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.pigeon.Activities.Adapters.MessageListAdapter;
import com.example.pigeon.FirebaseManagers.Messaging.MessagingHelper;
import com.example.pigeon.R;


public class MessagingRoomActivity extends AppCompatActivity {

    private static String chatTitle;
    private static String prevChatMessage;
    private static long timestamp;

    private ImageButton backButton;
    private TextView otherName;
    private ImageView profile;
    private ImageView photos;
    private ImageView cam;
    private EditText messageInput;
    private ImageView emoteButton;
    private ImageButton sendButton;

    private ListView messageList;
    public static MessageListAdapter messageListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging_room);
        backButton = findViewById(R.id.backButton);
        otherName = findViewById(R.id.otherName);
        profile = findViewById(R.id.profileMessage);
        photos = findViewById(R.id.photos);
        cam = findViewById(R.id.cam);
        messageInput = findViewById(R.id.messageInput);
        emoteButton = findViewById(R.id.emoteButton);
        sendButton = findViewById(R.id.sendButton);

        messageList = findViewById(R.id.messageList);

        otherName.setText(chatTitle);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainMenuActivity.class);
                v.getContext().startActivity(intent);
            }
        });
        //TODO: this should be on key pressed not on click
        messageInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!messageInput.getText().toString().isEmpty()){
                    sendButton.setBackgroundTintList(view.getContext().getResources().getColorStateList(R.color.colorButtons));
                } else {
                    sendButton.setBackgroundTintList(view.getContext().getResources().getColorStateList(R.color.deselected));
                }
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!messageInput.getText().toString().isEmpty()){
                    System.out.println("Sending");
                    String message = messageInput.getText().toString();
                    MessagingHelper.sendTextMessage(message);
                }
            }
        });

        messageListAdapter = new MessageListAdapter(this, R.id.userMessage);
        messageList.setAdapter(messageListAdapter);
        messageListAdapter.addAll(MessagingHelper.currentChatRoom);


    }

    public static void setChatInfo(MessagingHelper.ChatInfo c) {
        chatTitle = c.title;
        prevChatMessage = c.previousMessage;
        timestamp = c.TimeCreated;
    }

}
