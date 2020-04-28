package com.example.screentimer;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

class DataManager {
    private SimpleDateFormat formatter;
    private final String DATE_FORMAT = "dd/MM/yyyy";
    private final String SHARED_PREFS = "sharePrefs";
    private final String TIME = "time";
    private final String DATE = "date";
    private Context context;

    DataManager(Context c) {
        context = c;
    }

    void saveDailyScreenDuration(String date, int screenDuration){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        formatter = new java.text.SimpleDateFormat(DATE_FORMAT, Locale.FRANCE);
        editor.putInt(date, screenDuration);
        editor.apply();
    }


    void saveScreenDuration(int screenDuration) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        formatter = new java.text.SimpleDateFormat(DATE_FORMAT, Locale.FRANCE);
        editor.putInt(TIME, screenDuration);
        editor.putString(DATE, formatter.format(new Date()));
        editor.apply();
    }

    /* Returns duration in seconds stored in shared preferences or 0 */
    String loadDate() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        return sharedPreferences.getString(DATE, formatter.format(new Date()));
    }

    /* Returns duration in seconds stored in shared preferences or 0 */
    int loadScreenDuration() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        return sharedPreferences.getInt(TIME, 0);
    }

    /* Returns whether date stored is equal to date of day */
    boolean isTodayNewDayOfData() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        formatter = new SimpleDateFormat(DATE_FORMAT, Locale.FRANCE);
        return !sharedPreferences.getString(DATE, formatter.format(new Date())).equals(formatter.format(new Date()));
    }
}
