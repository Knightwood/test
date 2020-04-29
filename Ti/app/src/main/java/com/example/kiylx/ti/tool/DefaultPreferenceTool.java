package com.example.kiylx.ti.tool;

import android.content.Context;

import androidx.preference.PreferenceManager;

/**
 * 创建者 kiylx
 * 创建时间 2020/4/29 16:11
 */
public class DefaultPreferenceTool {

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, defaultValue);
    }

    public static String getStrings(Context context, String key, String defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, defaultValue);
    }

    public static int getInt(Context context, String key, int defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(key, defaultValue);
    }

}
