package com.example.pigeon.Activities.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.pigeon.Activities.MainActivity;
import com.example.pigeon.Activities.MessagingRoomActivity;
import com.example.pigeon.FirebaseManagers.FirebaseHelper;
import com.example.pigeon.FirebaseManagers.Messaging.MessagingHelper;
import com.example.pigeon.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChatListAdapter extends ArrayAdapter<HashMap<String, MessagingHelper.ChatInfo>> {

    private static List<String> ids = new ArrayList<>();

    public ChatListAdapter(Context context, int resource, List<HashMap<String, MessagingHelper.ChatInfo>> objects) {
        super(context, resource, objects);
    }
    public ChatListAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Gets the current item which should be the chat
        Map<String, MessagingHelper.ChatInfo> currItem = getItem(position);

        //Inflate the view
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_menu_item, parent, false);
        }

        //Set the textviews with their respective ids
        final TextView chatTitle = convertView.findViewById(R.id.chatTitle);
        TextView previousMessage = convertView.findViewById(R.id.previousMessage);
        TextView timeStamp = convertView.findViewById(R.id.timeStamp);

        //Get the set of our chat info
        Set<Map.Entry<String, MessagingHelper.ChatInfo>> entries = currItem.entrySet();

        if(currItem != null){
            //Iterate through
            Iterator<Map.Entry<String, MessagingHelper.ChatInfo>> iterator = entries.iterator();
            Map.Entry<String, MessagingHelper.ChatInfo> entry = null;
            while (iterator.hasNext()) {
                entry = iterator.next(); //Sets entry as the map value for our chat info
            }

            final MessagingHelper.ChatInfo chatInfo = entry.getValue(); //gets chat info

            String chatId = entry.getKey();

            FirebaseHelper.messagingDB.getReference("Chat Members").child(chatId).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if (dataSnapshot.exists()) {
                        StringBuilder sb = new StringBuilder();
                        if(chatInfo.title.equals("Title")){
                            String id = dataSnapshot.getValue(String.class);
                            if(id != MainActivity.user.getuID()){
                                sb.append(id);
                                chatTitle.setText(sb.toString());
                                MessagingRoomActivity.setTitle(sb.toString());
                            }
                        }

                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            //Sets the chat title and previousMessage into textviews
            //chatTitle.setText(chatInfo.title);
            previousMessage.setText(chatInfo.previousMessage);

            //Creates the time in normal form h:mm pm/am
            Date date = new Date(chatInfo.TimeCreated);
            SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a");
            String time = dateFormat.format(date);

            //Sets the time into the textview
            timeStamp.setText(time.toLowerCase());
        }

        return convertView;
    }
}
