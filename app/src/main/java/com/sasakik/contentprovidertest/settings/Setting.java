package com.sasakik.contentprovidertest.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by nine_eyes on 2016/06/03.
 */
public class Setting {

    public static class DBGSetting {
        public static final boolean FORCE_INMEMORYDB = false;
    }

    public static final String TAG_DEBUG_MODE = "debug_mode";
    Context mContext;

    public Setting(Context context) {
        mContext = context;
    }

    private SharedPreferences getLocalSharePrefs() {
        SharedPreferences prefs = null;
        if (mContext != null) {
            prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
//            prefs = mContext.getSharedPreferences(KEYVALUE_CSLOCAL, Context.MODE_PRIVATE);
        }
        return prefs;
    }

    private SharedPreferences.Editor getLocalSharePrefsEditor() {
        SharedPreferences.Editor editor = null;
        SharedPreferences prefs = getLocalSharePrefs();
        if (prefs != null) {
            editor = prefs.edit();
        }

        return editor;
    }


    public boolean isDbgMode() {
        boolean result = false;
        SharedPreferences prefs = getLocalSharePrefs();
        if (prefs != null) {
            result = prefs.getBoolean(TAG_DEBUG_MODE, false);
        }

        return result;
    }


    public void setDbgMode(boolean newvalue) {
        SharedPreferences.Editor editor = getLocalSharePrefsEditor();
        if (editor != null) {
            editor.putBoolean(TAG_DEBUG_MODE, newvalue);
            editor.commit();
        }
    }
}
