package org.telegram.hojjat;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by hojjatimani on 7/24/2016 AD.
 */
public class PreferenceManager {
    private static volatile PreferenceManager instance;
    public static final String prefsName = "Oddgram_Prefs";
    private SharedPreferences prefs;

    public static final String GHOAST_MODE_ENABLED = "GHOAST_MODE_ENABLED";

    public static PreferenceManager getInstance(Context context) {
        PreferenceManager localInstance = instance;
        if (localInstance == null) {
            synchronized (PreferenceManager.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new PreferenceManager(context);
                }
            }
        }
        return localInstance;
    }

    public PreferenceManager(Context context) {
        prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE);
    }

    public void saveBoolean(String key, boolean value) {
        prefs.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key, boolean defaultVal) {
        return prefs.getBoolean(key, defaultVal);
    }
}
