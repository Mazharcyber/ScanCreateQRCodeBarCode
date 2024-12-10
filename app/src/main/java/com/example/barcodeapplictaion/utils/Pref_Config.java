package com.example.barcodeapplictaion.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Pref_Config {
    public static final String KEY_UPDATE = "KEY_UPDATE";
    private static final String KEY_TIME = "KEY_TIME";
    SharedPreferences.Editor editor;
    private Context mContext;
    static Pref_Config myInstance;

    public static final String PREFERNCES_VIBRATE = "isvibrate";
    public static final String PREFERNCES_his = "PREFERNCES_his";
    public static final String PREFERNCES_COPY = "PREFERNCES_copy";

    public static void setVibrate(Context context, boolean value)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putBoolean(PREFERNCES_VIBRATE, value).apply();
    }

    public static boolean isVibrate(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(PREFERNCES_VIBRATE, true);
    }

    public static void setHistory(Context context, boolean value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putBoolean(PREFERNCES_his, value).apply();
    }

    public static boolean isHistory(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(PREFERNCES_his, true);
    }


    public static void setCopy(Context context, boolean value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putBoolean(PREFERNCES_COPY, value).apply();
    }

    public static boolean isCopy(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(PREFERNCES_COPY, true);
    }
}
