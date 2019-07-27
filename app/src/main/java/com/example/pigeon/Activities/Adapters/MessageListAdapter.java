package com.example.pigeon.Activities.Adapters;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.pigeon.Activities.MainActivity;
import com.example.pigeon.FirebaseManagers.Messaging.MessageList;
import com.example.pigeon.FirebaseManagers.Messaging.MessagingInstance;
import com.example.pigeon.FirebaseManagers.Messaging.TextMessage;
import com.example.pigeon.R;

import java.util.ArrayList;
import java.util.List;

public class MessageListAdapter extends BaseAdapter {

    private static final int TYPE_USER = 0;
    private static final int TYPE_OTHER = 1;
    private static final int TYPE_MAX = 2;

    private List<MessagingInstance> messageList;
    private LayoutInflater inflater;

    public MessageListAdapter(Context context) {
        messageList = new MessageList<>();
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    public void addList(List<MessagingInstance> messageList){
        if(messageList != null){
            if(!messageList.isEmpty()){
                this.messageList.clear();
            }
            this.messageList.addAll(messageList);
            notifyDataSetChanged();
        }
    }

    public void add(MessagingInstance message){
        this.messageList.add(message);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return this.messageList.size();
    }

    @Override
    public Object getItem(int i) {
        return this.messageList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder viewHolder = null;
        int type = getItemViewType(position);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)parent.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        if(convertView == null) {
            viewHolder = new ViewHolder();
            switch (type) {
                case TYPE_USER:
                    convertView = inflater.inflate(R.layout.chat_text_user, null);
                    viewHolder.textView = convertView.findViewById(R.id.userMessage);
                    viewHolder.textView.setMaxWidth((int)(width/1.5));
                    break;
                case TYPE_OTHER:
                    convertView = inflater.inflate(R.layout.chat_text_other, null);
                    viewHolder.textView = convertView.findViewById(R.id.otherMessage);
                    viewHolder.textView.setMaxWidth((int)(width/1.5));
                    break;
            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(messageList.get(position).getType().equals("TEXT")) {
            MessagingInstance message = (TextMessage)getItem(position);
            viewHolder.textView.setText(((TextMessage) message).getMessage());
        }
        //TODO: ADD IMAGE MESSAGE TYPE
        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX;
    }

    @Override
    public int getItemViewType(int position) {
        //If the item position's message
        return messageList.get(position).getUserID().equals(MainActivity.user.getuID()) ? TYPE_USER : TYPE_OTHER;
    }


    public static class ViewHolder {
        public TextView textView;
    }


}
