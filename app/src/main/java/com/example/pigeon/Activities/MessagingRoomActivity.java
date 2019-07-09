package com.example.pigeon.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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

        otherName.setText(chatTitle);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainMenuActivity.class);
                v.getContext().startActivity(intent);
            }
        });

    }

    public static void setChatInfo(MessagingHelper.ChatInfo c) {
        chatTitle = c.title;
        prevChatMessage = c.previousMessage;
        timestamp = c.TimeCreated;
    }

}
