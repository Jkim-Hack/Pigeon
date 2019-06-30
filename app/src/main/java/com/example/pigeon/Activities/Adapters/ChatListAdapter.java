package com.example.pigeon.Activities.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.pigeon.FirebaseManagers.Messaging.MessagingHelper;
import com.example.pigeon.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChatListAdapter extends ArrayAdapter<HashMap<String, MessagingHelper.ChatInfo>> {


    public ChatListAdapter(Context context, int resource, List<HashMap<String, MessagingHelper.ChatInfo>> objects) {
        super(context, resource, objects);
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
        TextView chatTitle = convertView.findViewById(R.id.chatTitle);
        TextView previousMessage = convertView.findViewById(R.id.previousMessage);
        TextView timeStamp = convertView.findViewById(R.id.timeStamp);

        //Get the set of our chat info
        Set<Map.Entry<String, MessagingHelper.ChatInfo>> entries = currItem.entrySet();

        //Iterate through
        Iterator<Map.Entry<String, MessagingHelper.ChatInfo>> iterator = entries.iterator();
        Map.Entry<String, MessagingHelper.ChatInfo> entry = null;
        while (iterator.hasNext()) {
            entry = iterator.next(); //Sets entry as the map value for our chat info
        }

        MessagingHelper.ChatInfo chatInfo = entry.getValue(); //gets chat info

        //Sets the chat title and previousMessage into textviews
        chatTitle.setText(chatInfo.title);
        previousMessage.setText(chatInfo.previousMessage);

        //Creates the time in normal form h:mm pm/am
        Date date = new Date(chatInfo.TimeCreated);
        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a");
        String time = dateFormat.format(date);

        //Sets the time into the textview
        timeStamp.setText(time.toLowerCase());


        return convertView;
    }
}
