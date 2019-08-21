package com.example.pigeon.Activities.Adapters;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pigeon.Activities.MainActivity;
import com.example.pigeon.FirebaseManagers.Messaging.Imaging.ImageHandler;
import com.example.pigeon.FirebaseManagers.Messaging.Imaging.ImageMessage;
import com.example.pigeon.FirebaseManagers.Messaging.MessageList;
import com.example.pigeon.FirebaseManagers.Messaging.MessagingInstance;
import com.example.pigeon.FirebaseManagers.Messaging.Text.TextMessage;
import com.example.pigeon.R;

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

    //Adds an entire list of Messaging Instances into the messageList
    public void addList(List<MessagingInstance> messageList){
        if(messageList != null){
            if(!messageList.isEmpty()){
                this.messageList.clear();
            }
            this.messageList.addAll(messageList);
            notifyDataSetChanged();
        }
    }

    //Adds a new MessagingInstance into the message list
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
        ViewHolder viewHolder = null; //View holder of a TextView
        int type = getItemViewType(position);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)parent.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics); //Gets metrics of the screen
        int width = displayMetrics.widthPixels; //Gets the width of the entire screen
        if(convertView == null) {
            viewHolder = new ViewHolder();
            switch (type) {
                case TYPE_USER: //Type of message as this user
                    if(messageList.get(position).getType().equals("TEXT")){
                        convertView = inflater.inflate(R.layout.item_chat_text_user, null); //Inflate the chat user's view
                        viewHolder.textView = convertView.findViewById(R.id.userMessage); //The id of the chat user's view TextView
                        viewHolder.textView.setMaxWidth((int)(width/1.5)); //Set the max width to a little more than half of the screen
                    } else if(messageList.get(position).getType().equals("IMAGE")) {
                        convertView = inflater.inflate(R.layout.item_chat_image_user, null);  //Inflate the chat user's view
                        viewHolder.imageView = convertView.findViewById(R.id.image_user); //The id of the chat user's view ImageView
                        viewHolder.imageView.setMaxWidth((int) (width / 1.5)); //Set the max width to a little more than half of the screen
                    }
                    break;
                case TYPE_OTHER: //Type of message as other user
                    if(messageList.get(position).getType().equals("TEXT")) {
                        convertView = inflater.inflate(R.layout.item_chat_text_other, null);  //Inflate the chat user's view
                        viewHolder.textView = convertView.findViewById(R.id.otherMessage); //The id of the chat user's view TextView
                        viewHolder.textView.setMaxWidth((int) (width / 1.5)); //Set the max width to a little more than half of the screen
                    } else if(messageList.get(position).getType().equals("IMAGE")) {
                        convertView = inflater.inflate(R.layout.item_chat_image_other, null);  //Inflate the chat user's view
                        viewHolder.imageView = convertView.findViewById(R.id.image_other); //The id of the chat user's view ImageView
                        viewHolder.imageView.setMaxWidth((int) (width / 1.5)); //Set the max width to a little more than half of the screen
                    }
                    break;
            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(messageList.get(position).getType().equals("TEXT")) {
            MessagingInstance message = (TextMessage)getItem(position); //Get the message at the current position
            viewHolder.textView.setText(((TextMessage) message).getMessage()); //Sets text of the view acquired above to the message received
        } else if(messageList.get(position).getType().equals("IMAGE")){
            MessagingInstance message = (ImageMessage)getItem(position);
            String downloadPath = ((ImageMessage) message).getDownloadPath();
            //TODO: ADD IN STAND IMAGE/ANIMATION WHILE THE REAL IMAGE LOADS
            ImageHandler.getImageFromPathBytes(downloadPath, viewHolder.imageView);
        }
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
        public ImageView imageView;
    }


}
