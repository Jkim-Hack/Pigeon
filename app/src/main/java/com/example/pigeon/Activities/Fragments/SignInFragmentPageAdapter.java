package com.example.pigeon.Activities.Fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SignInFragmentPageAdapter extends FragmentPagerAdapter {
    private String tabTitles[] = new String[] { "Sign In", "Sign Up"};
    private Context context;

    public SignInFragmentPageAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0)
            return SignInFragment.newInstance(position);
        else if(position == 1)
            return SignUpFragment.newInstance(position); //TODO: Create SignUpFragment
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }

}
