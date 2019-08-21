package com.example.pigeon.Activities.Adapters.Contacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pigeon.Activities.MainActivity;
import com.example.pigeon.FirebaseManagers.Accounts.ContactsHelper;
import com.example.pigeon.FirebaseManagers.Messaging.MessagingHelper;
import com.example.pigeon.R;
import com.example.pigeon.common.UserInfo.ContactInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ContactsListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private HashMap<String, ContactInfo> contactInfoMap;

    public ContactsListAdapter(Context context) {
        contactInfoMap = new HashMap<>();
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void putAll(HashMap<String, ContactInfo> map){
        contactInfoMap.putAll(map);
        notifyDataSetChanged();
    }

    public void put(String key, ContactInfo contactInfo){
        contactInfoMap.put(key, contactInfo);
        notifyDataSetChanged();
    }

    public void remove(String key){
        contactInfoMap.remove(key);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Map.Entry<String, ContactInfo> item = getItem(position);

        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_contact_user, parent, false);
        }

        TextView contactName = convertView.findViewById(R.id.contact_name);
        ImageView profileImage = convertView.findViewById(R.id.contact_image);
        Button toMessages = convertView.findViewById(R.id.messageButton);
        Button deleteContact = convertView.findViewById(R.id.deleteContact);

        final Context context = convertView.getContext();

        System.out.println(item + "OOOOOOOOOO");

        contactName.setText(item.getValue().getName());
        final String chatID = item.getValue().getChatID();
        toMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chatID != null){
                    MessagingHelper.LoadChatRoom(chatID, null, context);
                } else {
                    List<String> uids = new ArrayList<>();
                    uids.add(MainActivity.user.getuID());
                    uids.add(item.getValue().getUserID());
                    MessagingHelper.createChat(uids, context);
                }
            }
        });

        deleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactsHelper.deleteContact(item.getKey());
            }
        });

        return convertView;
    }

    @Override
    public int getCount() {
        return contactInfoMap.size();
    }

    @Override
    public Map.Entry<String, ContactInfo> getItem(int position) {
        Map.Entry<String, ContactInfo> entry = (Map.Entry<String, ContactInfo>) contactInfoMap.entrySet().toArray()[position];
        return entry;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
