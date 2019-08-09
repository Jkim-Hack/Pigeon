package com.example.pigeon.Activities.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.pigeon.Activities.Adapters.ChatListAdapter;
import com.example.pigeon.Activities.MainActivity;
import com.example.pigeon.FirebaseManagers.Messaging.MessagingHelper;
import com.example.pigeon.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static com.example.pigeon.Activities.MainMenuActivity.chatListAdapter;

public class ChatsFragment extends Fragment {


    public static ChatsFragment chatsFragmentBuilder(int position) {
        Bundle args = new Bundle();
        args.putInt("ARG_PAGE", position);
        ChatsFragment chatsFragment = new ChatsFragment();
        chatsFragment.setArguments(args);
        return chatsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chats_fragment_content, container, false);

        //Creates the new ListView object and adapter object
        final ListView chatList = view.findViewById(R.id.chatList);
        chatList.setAdapter(chatListAdapter); //Add the adapter to the list view

        //Loads the chat selected
        chatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, MessagingHelper.ChatInfo> item =
                        (HashMap<String, MessagingHelper.ChatInfo>)chatList.getItemAtPosition(position);
                Set<Map.Entry<String, MessagingHelper.ChatInfo>> entries = item.entrySet();
                System.out.println(entries);
                if(entries != null) {
                    //Iterate through
                    Iterator<Map.Entry<String, MessagingHelper.ChatInfo>> iterator = entries.iterator();
                    Map.Entry<String, MessagingHelper.ChatInfo> entry = null;
                    while (iterator.hasNext()) {
                        entry = iterator.next(); //Sets entry as the map value for our chat info
                    }
                    String chatID = entry.getKey();
                    System.out.println(chatID);
                    MessagingHelper.LoadChatRoom(chatID, getActivity(), view.getContext()); //Loads the chatroom
                }
            }
        });

        //Creates a new chat with the specified uid
        Button createButton = view.findViewById(R.id.createChatButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> list = new ArrayList<>();
                list.add(MainActivity.user.getuID());
                list.add("AIbFBafwzheXXBdjGh4fp8HnFO12");
                MessagingHelper.createChat(list, view.getContext());
            }
        });
        return view;
    }
}
