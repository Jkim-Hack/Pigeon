package com.example.pigeon.Activities.Adapters.Contacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pigeon.FirebaseManagers.Accounts.ContactsHelper;
import com.example.pigeon.R;
import com.example.pigeon.common.UserInfo.ContactInfo;

import java.util.HashMap;
import java.util.Map;

public class ContactRequestsAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private HashMap<String, ContactInfo> contactInfoMap;

    public ContactRequestsAdapter(Context context) {
        contactInfoMap = new HashMap<>();
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void add(String key, ContactInfo contactInfo) {
        contactInfoMap.put(key, contactInfo);
        notifyDataSetChanged();
    }

    public void remove(String key) {
        contactInfoMap.remove(key);
        notifyDataSetChanged();
    }

    public void replace(String key, ContactInfo contactInfo) {
        contactInfoMap.replace(key, contactInfo);
        notifyDataSetChanged();
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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Map.Entry<String,ContactInfo> contactInfoEntry = getItem(position);
        ViewHolder viewHolder = null;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.contact_request_adapter_item, parent, false);

            viewHolder.profileImage = convertView.findViewById(R.id.requestContactImage);
            viewHolder.name = convertView.findViewById(R.id.requestTextName);
            viewHolder.time = convertView.findViewById(R.id.requestTextTime);

            viewHolder.acceptButton = convertView.findViewById(R.id.requestAcceptButton);
            viewHolder.ignoreButton = convertView.findViewById(R.id.requestIgnoreButton);

            viewHolder.name.setText(contactInfoEntry.getValue().getName());

            final String key = contactInfoEntry.getKey();
            viewHolder.acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ContactsHelper.acceptContact(contactInfoEntry.getValue(), key);
                }
            });
            viewHolder.ignoreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ContactsHelper.ignoreContact(contactInfoEntry.getValue(), key);
                }
            });
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    private class ViewHolder {
        public TextView name;
        public TextView time;
        public ImageView profileImage;
        public Button acceptButton;
        public Button ignoreButton;
    }

}
