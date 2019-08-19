package com.example.pigeon.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SavedSharedPreferences {

    private static String PREF_LOGINEMAIL = "email";
    private static String PREF_LOGINPASSWORD = "password";

    public static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setLogin(Context ctx, String email, String password)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_LOGINEMAIL, email);
        editor.putString(PREF_LOGINPASSWORD, password);
        editor.commit();
    }

    public static String getEmail(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_LOGINEMAIL, "");
    }

    public static String getPassword(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_LOGINPASSWORD, "");
    }

}
