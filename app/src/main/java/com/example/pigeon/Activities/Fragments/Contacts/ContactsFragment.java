package com.example.pigeon.Activities.Fragments.Contacts;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.pigeon.FirebaseManagers.Accounts.ContactsHelper;
import com.example.pigeon.R;

public class ContactsFragment extends Fragment {

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

        ViewPager viewPager = view.findViewById(R.id.contactsFragmentViewPager);
        viewPager.setAdapter(new ContactsPageFragmentPagerAdapter(this.getChildFragmentManager()));

        TabLayout tabLayout = view.findViewById(R.id.contactsTabLayout);
        tabLayout.setupWithViewPager(viewPager);


        Button newContactButton = view.findViewById(R.id.newContactButton);
        newContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactsHelper.requestContact("aTfrH9fwbtNI0hRYGg0r7MkwOCU2");
            }
        });

        return view;
    }


}
