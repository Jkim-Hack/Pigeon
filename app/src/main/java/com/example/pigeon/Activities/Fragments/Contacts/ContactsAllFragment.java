package com.example.pigeon.Activities.Fragments.Contacts;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationManagerCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.pigeon.Activities.MainActivity;
import com.example.pigeon.FirebaseManagers.Accounts.ContactsHelper;
import com.example.pigeon.FirebaseManagers.FirebaseHelper;
import com.example.pigeon.R;
import com.example.pigeon.common.NotificationHelper;
import com.example.pigeon.common.UserInfo.ContactInfo;

import java.util.List;

import static com.example.pigeon.Activities.MainMenuActivity.contactsListAdapter;

public class ContactsAllFragment extends Fragment {

    private ListView contactsList;

    public static ContactsAllFragment newInstance(int position) {
        Bundle args = new Bundle();
        args.putInt("ARG_PAGE", position);
        ContactsAllFragment fragment = new ContactsAllFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        List<Integer> list = NotificationHelper.contactMadeNotifIds;
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this.getActivity());
        if(list != null){
            for(Integer i : list){
                notificationManager.cancel(i);
            }
            list.clear();
            FirebaseHelper.notifsDB.getReference().child(MainActivity.user.getuID()).child(ContactsHelper.CONTACTSLIST).removeValue();
        }
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contact_all_fragment, container, false);


        contactsList = view.findViewById(R.id.contactsList);
        //System.out.println(contactsListAdapter.getItem(0) + "---------------------------");
        contactsList.setAdapter(contactsListAdapter);

        contactsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContactInfo contactInfo = (ContactInfo)contactsList.getItemAtPosition(position);
                //TODO: Move to the user's activity
            }
        });


        return view;
    }
}
