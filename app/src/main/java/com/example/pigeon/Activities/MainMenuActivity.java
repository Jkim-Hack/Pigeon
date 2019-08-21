package com.example.pigeon.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.pigeon.Activities.Adapters.ChatListAdapter;
import com.example.pigeon.Activities.Adapters.Contacts.ContactRequestsAdapter;
import com.example.pigeon.Activities.Adapters.Contacts.ContactsListAdapter;
import com.example.pigeon.Activities.Fragments.ChatsFragment;
import com.example.pigeon.Activities.Fragments.Contacts.ContactsFragment;
import com.example.pigeon.FirebaseManagers.Accounts.ContactsHelper;
import com.example.pigeon.FirebaseManagers.Messaging.MessagingHelper;
import com.example.pigeon.R;
import com.example.pigeon.common.UserInfo.ContactInfo;
import com.example.pigeon.common.NotificationHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainMenuActivity extends AppCompatActivity {

    public static ChatListAdapter chatListAdapter; //Chat list adapter is what fills out the info in the ListView of chats

    public static List<ContactInfo> tempContactsList = new ArrayList<>();
    public static ContactsListAdapter contactsListAdapter;
    public static ContactRequestsAdapter contactRequestsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(MainActivity.user == null){
            Intent intent = new Intent(this, StartingActivity.class);
            this.startActivity(intent);
        }

        setContentView(R.layout.activity_main_menu);
        try {
            System.out.println(MainActivity.user.getName());
        } catch (NullPointerException e){
            System.out.println("DD");
        }
        NotificationHelper.createNotificationChannel(this,   "PIGEON "+ "CHANNEL");
        chatListAdapter = new ChatListAdapter(this, R.layout.item_chat_menu);
        MessagingHelper.LoadAllChatRooms(chatListAdapter); //Loads all chat rooms

        contactsListAdapter = new ContactsListAdapter(this);
        contactRequestsAdapter = new ContactRequestsAdapter(this);

        contactsListAdapter.putAll(ContactsHelper.contacts);
        Iterator<Map.Entry<String, ContactInfo>> iterator = ContactsHelper.contactRequests.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ContactInfo> entry = iterator.next();
            String key = entry.getKey();
            ContactInfo val = entry.getValue();
            contactRequestsAdapter.add(key, val);
        }

        final FragmentManager fragmentManager = getSupportFragmentManager();

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                Fragment fragment;
                switch (menuItem.getItemId()) {
                    case R.id.chatsNav:
                        fragment = ChatsFragment.chatsFragmentBuilder(0);
                        break;
                    case R.id.contactsNav:
                        fragment = ContactsFragment.contactsFragmentBuilder(1);
                        break;
                    default:
                        fragment = ChatsFragment.chatsFragmentBuilder(0);
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.main_menu_container, fragment).commit();
                return true;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.chatsNav);
    }

}
