package com.example.pigeon.Activities.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.pigeon.Activities.Adapters.ContactsListAdapter;
import com.example.pigeon.R;
import com.example.pigeon.common.ContactInfo;

public class ContactsFragment extends Fragment {

    public static ContactsListAdapter contactsListAdapter;

    public static ContactsFragment contactsFragmentBuilder(int position) {
        Bundle args = new Bundle();
        args.putInt("ARG_PAGE", position);
        ContactsFragment contactsFragment = new ContactsFragment();
        contactsFragment.setArguments(args);
        return contactsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contacts_fragment_content, container, false);

        final ListView contactsList = view.findViewById(R.id.contactsList);
        contactsListAdapter = new ContactsListAdapter(view.getContext(), R.layout.contact_user);
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
