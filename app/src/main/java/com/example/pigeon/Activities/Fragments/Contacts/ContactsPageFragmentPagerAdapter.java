package com.example.pigeon.Activities.Fragments.Contacts;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ContactsPageFragmentPagerAdapter extends FragmentPagerAdapter {

    private String[] fragmentTitles = {"All", "Requests"};
    private static final int COUNT = 2;
    public ContactsPageFragmentPagerAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment;
        switch (i) {
            case 0:
                fragment = ContactsAllFragment.newInstance(0);
                return fragment;
            case 1:
                fragment = ContactRequestsFragment.newInstance(1);
                return fragment;
        }
        return null;
    }


    @Override
    public int getCount() {
        return COUNT;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitles[position];
    }
}
