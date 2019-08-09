package com.example.pigeon.Activities.Adapters.Contacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pigeon.Activities.MainActivity;
import com.example.pigeon.FirebaseManagers.Messaging.MessagingHelper;
import com.example.pigeon.R;
import com.example.pigeon.common.ContactInfo;

import java.util.ArrayList;
import java.util.List;


public class ContactsListAdapter extends ArrayAdapter<ContactInfo> {

    public ContactsListAdapter(Context context, int resource, List<ContactInfo> contactInfoList){
        super(context,resource ,contactInfoList);
    }

    public ContactsListAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ContactInfo item = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.contact_user, parent, false);
        }

        TextView contactName = convertView.findViewById(R.id.contact_name);
        ImageView profileImage = convertView.findViewById(R.id.contact_image);
        Button toMessages = convertView.findViewById(R.id.messageButton);

        final Context context = convertView.getContext();

        System.out.println(item + "OOOOOOOOOO");

        contactName.setText(item.getName());
        final String chatID = item.getChatID();
        toMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chatID != null){
                    MessagingHelper.LoadChatRoom(chatID, null, context);
                } else {
                    List<String> uids = new ArrayList<>();
                    uids.add(MainActivity.user.getuID());
                    uids.add(item.getUserID());
                    MessagingHelper.createChat(uids, context);
                }
            }
        });


        return convertView;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }
}
