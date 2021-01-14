package com.codestown.examinoo.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.codestown.examinoo.Constant;

public class Session {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    public static final String SETTING_Quiz_PREF = "setting_quiz_pref";
    private static final String SOUND_ONOFF = "sound_enable_disable";
    private static final String SHOW_MUSIC_ONOFF = "showmusic_enable_disable";
    private static final String VIBRATION = "vibrate_status";
    public static final String TOTAL_SCORE = "total_score";
    public static final String POINT = "no_of_point";

    public static final String IS_LAST_LEVEL_COMPLETED = "is_last_level_completed";
    public static final String LAST_LEVEL_SCORE = "last_level_score";
    public static final String COUNT_QUESTION_COMPLETED = "count_question_completed";
    public static final String COUNT_RIGHT_ANSWARE_QUESTIONS = "count_right_answare_questions";


    public static final String LANG_MODE = "lang_mode";
    public static final String N_COUNT = "n_count";
    public static final String E_MODE = "e_mode";
    public static final String LOGIN = "login";
    public static final String USER_ID = "userId";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String MOBILE = "mobile";
    public static final String PROFILE = "profile";
    public static final String F_CODE = "f_code";
    public static final String IS_FIRST_TIME = "isfirsttime";
    public static final String REFER_CODE = "refer_code";
    public static final String LANGUAGE = "language";
    public static final String QUICK_ANS = "quick_ans";
    public static final String FCM = "fcm";

    public Session(Context context) {
        this._context = context;
        pref = PreferenceManager.getDefaultSharedPreferences(_context);
        editor = pref.edit();
    }

    public String getData(String id) {
        return pref.getString(id, "");
    }

    public void setData(String id, String val) {
        editor.putString(id, val);
        editor.commit();
    }

    // public static final String
    public static void setVibration(Context context, Boolean result) {
        SharedPreferences prefs = context.getSharedPreferences(SETTING_Quiz_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = prefs.edit();
        prefEditor.putBoolean(VIBRATION, result);
        prefEditor.apply();
    }

    public static boolean getVibration(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(SETTING_Quiz_PREF, Context.MODE_PRIVATE);
            return prefs.getBoolean(VIBRATION, Utils.DEFAULT_VIBRATION_SETTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Utils.DEFAULT_VIBRATION_SETTING;
    }

    public static void setSoundEnableDisable(Context context, Boolean result) {
        SharedPreferences prefs = context.getSharedPreferences(SETTING_Quiz_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = prefs.edit();
        prefEditor.putBoolean(SOUND_ONOFF, result);
        prefEditor.apply();
    }

    public static boolean getSoundEnableDisable(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(SETTING_Quiz_PREF, Context.MODE_PRIVATE);
            return prefs.getBoolean(SOUND_ONOFF, Utils.DEFAULT_SOUND_SETTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Utils.DEFAULT_SOUND_SETTING;
    }

    public static void setMusicEnableDisable(Context context, Boolean result) {
        SharedPreferences prefs = context.getSharedPreferences(SETTING_Quiz_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = prefs.edit();
        prefEditor.putBoolean(SHOW_MUSIC_ONOFF, result);
        prefEditor.apply();
    }

    public static boolean getMusicEnableDisable(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                SETTING_Quiz_PREF, Context.MODE_PRIVATE);
        return prefs.getBoolean(SHOW_MUSIC_ONOFF,
                Utils.DEFAULT_MUSIC_SETTING);
    }

    public static void setCurrentLanguage(String value, Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LANGUAGE, value);
        editor.apply();

    }

    //this method will fetch the device token from shared preferences
    public static String getCurrentLanguage(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(LANGUAGE, Constant.D_LANG_ID);
    }

    public static boolean getBoolean(String key, Context context) {
        // TODO Auto-generated method stub
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(key, false);
    }

    public static void setBoolean(String key, boolean value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static int getCountQuestionCompleted(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                SETTING_Quiz_PREF, Context.MODE_PRIVATE);
        return prefs.getInt(COUNT_QUESTION_COMPLETED, 1);
    }

    public static boolean isLevelCompleted(Context context) {
        // TODO Auto-generated method stub
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(IS_LAST_LEVEL_COMPLETED, false);
    }
    // /getPWDFromSP()

    public static void setLevelComplete(Context context, boolean value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(IS_LAST_LEVEL_COMPLETED, value);
        editor.apply();
    }

    //fireBase token
    public static void setDeviceToken(String token, Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.apply();
    }

    //this method will fetch the device token from shared preferences
    public static String getDeviceToken(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString("token", null);
    }


    public static void setFifty_Fifty(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("fifty_fifty", true);
        editor.apply();
    }

    public static boolean isFiftyFiftyUsed(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("fifty_fifty", false);
    }


    public static void setReset(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("reset", true);
        editor.apply();
    }

    public static boolean isResetUsed(Context context) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("reset", false);
    }

    public static void setAudiencePoll(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("audience", true);
        editor.apply();
    }

    public static boolean isAudiencePollUsed(Context context) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("audience", false);
    }


    public static void setSkip(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("skip", true);
        editor.apply();
    }

    public static boolean isSkipUsed(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("skip", false);
    }

    public static void setNCount(int value, Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(N_COUNT, value);
        editor.apply();
    }

    public static int getNCount(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(N_COUNT, 0);
    }

    public static void setFCode(String value, Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(F_CODE, value);
        editor.apply();
    }

    public static String getFCode(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(F_CODE, "");
    }

    public static void removeSharedPreferencesData(Context mContext) {
        if (mContext != null) {
            SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            if (mSharedPreferences != null) {
                mSharedPreferences.edit().remove("fifty_fifty").apply();
                mSharedPreferences.edit().remove("reset").apply();
                mSharedPreferences.edit().remove("audience").apply();
                mSharedPreferences.edit().remove("skip").apply();
            }
        }
    }

    public static void saveTextSize(Context context, String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constant.PREF_TEXTSIZE, value);
        editor.apply();
    }

    public static String getSavedTextSize(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(Constant.PREF_TEXTSIZE, Constant.TEXTSIZE_MIN);
    }


    public static String getUserData(String key, Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(key, null);
    }

    public static boolean isLogin(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(LOGIN, false);
    }

    public static void saveUserDetail(Context context, String userId, String name, String email, String mobile, String profile, String referCode) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_ID, userId);
        editor.putString(NAME, name);
        editor.putString(EMAIL, email);
        editor.putString(MOBILE, mobile);
        editor.putString(PROFILE, profile);
        editor.putString(REFER_CODE, referCode);
        editor.putBoolean(LOGIN, true);
        editor.apply();
    }


    public static void clearUserSession(Context mContext) {
        if (mContext != null) {
            SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            if (mSharedPreferences != null) {
                mSharedPreferences.edit().remove(USER_ID).apply();
                mSharedPreferences.edit().remove(NAME).apply();
                mSharedPreferences.edit().remove(EMAIL).apply();
                mSharedPreferences.edit().remove(MOBILE).apply();
                mSharedPreferences.edit().remove(LOGIN).apply();
                mSharedPreferences.edit().remove(PROFILE).apply();
                mSharedPreferences.edit().remove(LANGUAGE).apply();


            }
        }
    }


    public static void setUserData(String key, String value, Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
}
