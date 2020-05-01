package com.github.beinstag.screentime;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

class DataManager {

    private Context context;

    private final String DATE = "date";
    private final String SHARED_PREFS;
    private final String DATE_PREFS = "datePrefs";

    private final SimpleDateFormat dateFormat;
    DataManager(Context c) {
        context = c;
        String DATE_FORMAT = "dd/MM/yyyy";
        Locale loc = c.getResources().getConfiguration().locale;
        dateFormat = new SimpleDateFormat(DATE_FORMAT, loc);
        SHARED_PREFS = "sharePrefs";
    }

    void saveDailyScreenDuration(String date, int screenDuration) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(date, screenDuration);
        editor.apply();
    }

    private void saveTestData(SharedPreferences.Editor editor) {

        editor.putInt(dateFormat.format(yesterday(1)), 1000);
        editor.putInt(dateFormat.format(yesterday(2)), 2000);
        editor.putInt(dateFormat.format(yesterday(3)), 3000);
        editor.putInt(dateFormat.format(yesterday(4)), 4000);
        editor.putInt(dateFormat.format(yesterday(5)), 5000);
        editor.putInt(dateFormat.format(yesterday(6)), 6000);
        editor.putInt(dateFormat.format(yesterday(7)), 7000);
        editor.putInt(dateFormat.format(yesterday(8)), 8000);
        editor.putInt(dateFormat.format(yesterday(9)), 9000);
        editor.putInt(dateFormat.format(yesterday(10)), 10000);
        editor.putInt(dateFormat.format(yesterday(11)), 11000);
        editor.putInt(dateFormat.format(yesterday(12)), 12000);
        editor.putInt(dateFormat.format(yesterday(13)), 13000);
        editor.putInt(dateFormat.format(yesterday(14)), 14000);
        editor.putInt(dateFormat.format(yesterday(15)), 15000);
        editor.putInt(dateFormat.format(yesterday(16)), 16000);
        editor.putInt(dateFormat.format(yesterday(17)), 17000);
        editor.putInt(dateFormat.format(yesterday(18)), 18000);
        editor.putInt(dateFormat.format(yesterday(19)), 19000);
        editor.putInt(dateFormat.format(yesterday(20)), 20000);
        editor.putInt(dateFormat.format(yesterday(21)), 21000);
        editor.putInt(dateFormat.format(yesterday(22)), 22000);
        editor.putInt(dateFormat.format(yesterday(23)), 23000);
        editor.putInt(dateFormat.format(yesterday(24)), 24000);
        editor.putInt(dateFormat.format(yesterday(25)), 25000);
        editor.putInt(dateFormat.format(yesterday(26)), 26000);
        editor.putInt(dateFormat.format(yesterday(27)), 27000);
        editor.putInt(dateFormat.format(yesterday(28)), 28000);
        editor.putInt(dateFormat.format(yesterday(29)), 29000);
        editor.putInt(dateFormat.format(yesterday(30)), 30000);
        editor.putInt(dateFormat.format(yesterday(31)), 31000);
        editor.putInt(dateFormat.format(yesterday(32)), 32000);
        editor.putInt(dateFormat.format(yesterday(33)), 33000);
        editor.putInt(dateFormat.format(yesterday(34)), 34000);
        editor.putInt(dateFormat.format(yesterday(35)), 35000);
        editor.putInt(dateFormat.format(yesterday(36)), 36000);
        editor.putInt(dateFormat.format(yesterday(37)), 37000);
        editor.putInt(dateFormat.format(yesterday(38)), 38000);
        editor.putInt(dateFormat.format(yesterday(39)), 39000);
        editor.putInt(dateFormat.format(yesterday(40)), 40000);
        editor.putInt(dateFormat.format(yesterday(41)), 41000);
        editor.putInt(dateFormat.format(yesterday(42)), 42000);
        editor.putInt(dateFormat.format(yesterday(43)), 43000);
        editor.putInt(dateFormat.format(yesterday(44)), 44000);
        editor.putInt(dateFormat.format(yesterday(45)), 45000);
        editor.putInt(dateFormat.format(yesterday(46)), 46000);
        editor.putInt(dateFormat.format(yesterday(47)), 47000);
        editor.putInt(dateFormat.format(yesterday(48)), 48000);
        editor.apply();
    }


    /* Save screenDuration to Shared Preferences*/
    void saveScreenDuration(int screenDuration) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(dateFormat.format(new Date()), screenDuration);
        //saveTestData(editor);
        editor.apply();


        SharedPreferences datePreferences = context.getSharedPreferences(DATE_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor dateEditor = datePreferences.edit();
        editor.putString(DATE, dateFormat.format(new Date()));
        dateEditor.apply();
    }

    /* Returns last date app was used or today's date */
    String loadDate() {
        SharedPreferences datePreferences = context.getSharedPreferences(DATE_PREFS, MODE_PRIVATE);
        return datePreferences.getString(DATE, dateFormat.format(new Date()));
    }

    /* Returns duration of past ith day from now in seconds stored in shared preferences or 0 */
    int loadPastScreenDuration(int i) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        return sharedPreferences.getInt(dateFormat.format(yesterday(i)), 0);
    }

    /* Returns duration in seconds stored in shared preferences or 0 */
    int loadScreenDuration() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        return sharedPreferences.getInt(dateFormat.format(new Date()), 0);
    }

    /* Returns whether date stored (last date app was used) is different from date of day */
    boolean isTodayNewDayOfData() {
        return ! loadDate().equals(dateFormat.format(new Date()));
    }

    /*Returns date of the nth day in the past*/
    private Date yesterday(int i) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -i);
        return cal.getTime();
    }
}
