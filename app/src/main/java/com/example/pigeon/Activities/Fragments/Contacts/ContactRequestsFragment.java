package com.example.pigeon.Activities.Fragments.Contacts;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.example.pigeon.R;

import static com.example.pigeon.Activities.MainMenuActivity.contactRequestsAdapter;

public class ContactRequestsFragment extends Fragment {

    public static ContactRequestsFragment newInstance(int position){
        Bundle args = new Bundle();
        args.putInt("ARG_PAGE", position);
        ContactRequestsFragment fragment = new ContactRequestsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contact_requests_fragment, container, false);
        ListView listView = view.findViewById(R.id.requestList);
        listView.setAdapter(contactRequestsAdapter);
        return view;
    }
}
