package com.example.pigeon.Activities.Fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SignInFragmentPagerAdapter extends FragmentPagerAdapter {
    private String tabTitles[] = new String[] { "Sign In", "Sign Up"};
    private Context context;

    public SignInFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    //The amount of items, this case, 2
    @Override
    public int getCount() {
        return 2;
    }

    //Creates all the items within the fragment and places it in the pager adapter
    //In this case, one fragment for sign in and one for sign up
    @Override
    public Fragment getItem(int position) {
        if(position == 0)
            return SignInFragment.newInstance(position);
        else if(position == 1)
            return SignUpFragment.newInstance(position);
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }

}
