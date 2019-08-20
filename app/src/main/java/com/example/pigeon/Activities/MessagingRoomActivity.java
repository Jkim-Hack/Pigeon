package com.example.pigeon.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.pigeon.FirebaseManagers.FirebaseHelper;
import com.example.pigeon.FirebaseManagers.Messaging.MessagingHelper;
import com.example.pigeon.R;
import com.example.pigeon.common.UserInfo.ContactInfo;
import com.example.pigeon.common.NotificationHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static com.example.pigeon.FirebaseManagers.Messaging.MessagingHelper.currentChatID;


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
    //private android.support.v7.widget.Toolbar toolbar;

    private static ListView messageList;

    private boolean isClicked = false;

    private static final int REQUEST_IMAGE_CAPTURE = 1;


    @SuppressLint("ClickableViewAccessibility")
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

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        ArrayList<Integer> idList = NotificationHelper.chatNotificationIDs.get(currentChatID);
        if(idList != null){
            for(Integer i : idList){
                notificationManager.cancel(i);
            }
            idList.clear();
            FirebaseHelper.notifsDB.getReference().child(MainActivity.user.getuID()).child(currentChatID).removeValue();
        }

        //toolbar = findViewById(R.id.topbar);
        HashMap<String, ContactInfo> membersMap = MessagingHelper.chatMembers.get(currentChatID);
        Set<Map.Entry<String,ContactInfo>> members = membersMap.entrySet();

        Iterator<Map.Entry<String, ContactInfo>> membersIterators = members.iterator();
        StringBuilder sb = new StringBuilder();
        while (membersIterators.hasNext()){
            Map.Entry<String, ContactInfo> member = membersIterators.next();
            if(!member.getKey().equals(MainActivity.user.getuID())){
                if(members.size() > 2){
                    sb.append(member.getValue().getName());
                } else {
                    sb.append(member.getValue().getName() + ", ");
                }
            }
        }
        if(sb.charAt(sb.length()-2) == ','){
            sb.deleteCharAt(sb.length()-2);
            sb.deleteCharAt(sb.length()-1);
        }
        chatTitle = sb.toString();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


        final InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);

        otherName.setText(chatTitle);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainMenuActivity.class);
                v.getContext().startActivity(intent);
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!messageInput.getText().toString().isEmpty()) {
                    System.out.println("Sending");
                    String message = messageInput.getText().toString();
                    messageInput.setText("");
                    MessagingHelper.sendTextMessage(message);

                }
            }
        });


        messageList.setAdapter(MessagingHelper.adapters.get(currentChatID));

        final int width = messageInput.getWidth();

        messageList.setClickable(false);
        messageInput.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isClicked) {
                    inputMethodManager.showSoftInput(v, 0);
                    messageInput.setWidth((int) (messageInput.getWidth() * 1.25));
                    isClicked = true;
                    sendButton.setBackgroundTintList(v.getContext().getResources().getColorStateList(R.color.deselected));
                }
                return false;
            }
        });

        messageList.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        //messageList.setStackFromBottom(true);
        messageList.setScrollingCacheEnabled(true);
        messageList.setSmoothScrollbarEnabled(false);

        messageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isClicked) {
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    messageInput.setWidth(width);
                    isClicked = false;
                }
            }
        });

        messageInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (!messageInput.getText().toString().isEmpty()) {
                    sendButton.setBackgroundTintList(v.getContext().getResources().getColorStateList(R.color.colorButtons));
                } else {
                    sendButton.setBackgroundTintList(v.getContext().getResources().getColorStateList(R.color.deselected));
                }

                return false;
            }
        });

        /*
        toolbar.setTitle("Toolbar Test 1");
        toolbar.inflateMenu(R.menu.profile_icon_messaging_menu);
        toolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.ic_keyboard_arrow_left_24px, null));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainMenuActivity.class);
                v.getContext().startActivity(intent);
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.profile_settings) {
                    return true;
                }

                return false;
            }
        });
        */

    }

    public static void setChatInfo(MessagingHelper.ChatInfo c) {
        prevChatMessage = c.previousMessage;
        timestamp = c.TimeCreated;
    }

    public static void setUserInfo(String users0) {
        String[] users1 = users0.split(",");
        StringBuilder sb = new StringBuilder();
        for (String user : users1) {
            if (user != MainActivity.user.getuID()) {
                sb.append(user);
            }
        }
        chatTitle = sb.toString();
    }

    public static void setTitle(String chatTitle1) {
        chatTitle = chatTitle1;
    }


}
