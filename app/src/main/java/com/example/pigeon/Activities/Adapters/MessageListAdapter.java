package com.example.pigeon.Activities.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.pigeon.Activities.MainActivity;
import com.example.pigeon.FirebaseManagers.Messaging.MessagingInstance;
import com.example.pigeon.FirebaseManagers.Messaging.TextMessage;
import com.example.pigeon.R;

import java.util.List;

public class MessageListAdapter extends ArrayAdapter<MessagingInstance> {

    private static final int TYPE_USER = 0;
    private static final int TYPE_OTHER = 1;
    private static final int TYPE_MAX = 2;


    public MessageListAdapter(Context context, int resource, List<MessagingInstance> objects) {
        super(context, resource, objects);

    }

    public MessageListAdapter(Context context, int resource){
        super(context,resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder viewHolder = null;
        int type = getItemViewType(position);
        if(convertView == null) {
            viewHolder = new ViewHolder();
            switch (type) {
                case TYPE_USER:
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_text_user, null);
                    viewHolder.textView = convertView.findViewById(R.id.userMessage);
                    break;
                case TYPE_OTHER:
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_text_other, null);
                    viewHolder.textView = convertView.findViewById(R.id.otherMessage);
                    break;
            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(getItem(position).getType().equals("TEXT")) {
            MessagingInstance message = (TextMessage)getItem(position);
            viewHolder.textView.setText(((TextMessage) message).getMessage());
        } //TODO: ADD IMAGE MESSAGE TYPE
        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX;
    }

    @Override
    public int getItemViewType(int position) {
        //If the item position's message
        return this.getItem(position).getUserID().equals(MainActivity.user.getuID()) ? TYPE_USER : TYPE_OTHER;
    }


    public static class ViewHolder {
        public TextView textView;
    }


}
