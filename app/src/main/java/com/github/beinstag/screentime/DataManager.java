package com.github.beinstag.screentime;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

class DataManager {

    private final String DATE = "date";
    private final String HOUR = "hour";
    private SharedPreferences datePreferences;
    private SharedPreferences hourPreferences;
    private SharedPreferences sharedPreferences;
    private final SimpleDateFormat dateFormat, hourFormat;

    DataManager(Context c) {
        String DATE_FORMAT = "dd/MM/yyyy";
        String HOUR_FORMAT = "HH";
        Locale loc = c.getResources().getConfiguration().getLocales().get(0);
        dateFormat = new SimpleDateFormat(DATE_FORMAT, loc);
        hourFormat = new SimpleDateFormat(HOUR_FORMAT, loc);
        datePreferences = c.getSharedPreferences("datePrefs", MODE_PRIVATE);
        hourPreferences = c.getSharedPreferences("hourPrefs", MODE_PRIVATE);
        sharedPreferences = c.getSharedPreferences("sharePrefs", MODE_PRIVATE);
    }

    /**
     * Returns duration of past ith day from now in seconds stored in shared preferences or 0
     *
     * @param i number of day in the past
     * @return daily screen duration (in seconds) of the day i past days from now
     */
    int loadPastScreenDuration(int i) {
        return sharedPreferences.getInt(dateFormat.format(yesterday(i)), 0);
    }

    /**
     * Returns duration of past ith day from now in seconds stored in shared preferences or 0
     *
     * @param i nb of hours in the past
     * @return hourly screen duration (in seconds) of the hour i past hours from now
     */
    int loadPastHoursScreenDuration(int i) {
        return hourPreferences.getInt(hourFormat.format(pastHour(i)), 0);
    }

    /**
     * Returns duration in seconds stored in shared preferences or 0
     */
    int loadScreenDuration(Date date) {
        return sharedPreferences.getInt(dateFormat.format(date), 0);
    }

    int loadLastScreenDuration(){
        int lastScreenDuration = 0;
        for(int i = 0 ; lastScreenDuration == 0 ; ++i) {
            lastScreenDuration = sharedPreferences.getInt(dateFormat.format(yesterday(i)),0);
        }
        return lastScreenDuration;
    }

    int loadLastHourlyScreenDuration(){
        int lastHourlyScreenDuration = 0;
        for(int i = 0 ; lastHourlyScreenDuration == 0 ; ++i) {
            lastHourlyScreenDuration = hourPreferences.getInt(hourFormat.format(pastHour(i)),0);
        }
        return lastHourlyScreenDuration;
    }

    /**
     * Returns whether current time hour is a new hour
     *
     * @return true if current time hour is different from last time hour this DataManager was used
     */
    boolean isNewHourOfData(Date date) {
        return !loadHour().equals(hourFormat.format(date));
    }

    /**
     * Returns whether date stored (last date DataManager was used) is different from date of day
     *
     * @return true if current day is different from last day this DataManager was used
     */
    boolean isNewDayOfData(Date date) {
        return !loadDate().equals(dateFormat.format(date));
    }

    /**
     * Returns last date app was used or today's date (if it's used for the first time)
     *
     * @return last date app was used or today's date
     */
    private String loadDate() {
        return datePreferences.getString(DATE, dateFormat.format(new Date()));
    }

    /**
     * Returns hour HH when this Data Manager was last used or current HH if used for the first time
     *
     * @return hour HH when this Data Manager was last used
     */
    private String loadHour() {
        return datePreferences.getString(HOUR, hourFormat.format(new Date()));
    }

    /**
     * Saves the passed screenDuration to current by-hour storage (shared preferences)
     *
     * @param screenDuration hourly screen duration to be saved
     */
    void saveHScreenDuration(int screenDuration, Date date) {
        SharedPreferences.Editor hourEditor = hourPreferences.edit();
        hourEditor.putInt(hourFormat.format(date), screenDuration);
        hourEditor.apply();

        SharedPreferences.Editor dateEditor = datePreferences.edit();
        dateEditor.putString(HOUR, hourFormat.format(date));
        dateEditor.apply();
    }

    /**
     * Save screenDuration to Shared Preferences and saves current date as last saved date.
     */
    void saveScreenDuration(int screenDuration, Date date) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(dateFormat.format(date), screenDuration);
        editor.apply();

        SharedPreferences.Editor dateEditor = datePreferences.edit();
        dateEditor.putString(DATE, dateFormat.format(date));
        dateEditor.apply();
    }

    /**
     * Returns the date (and time) i hours n the past
     *
     * @param i number of hours in the past from now
     * @return past Date minus i hours
     */
    private Date pastHour(int i) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, -i);
        return cal.getTime();
    }

    /**
     * Returns the date (and time) i days n the past
     *
     * @param i number of days in the past from now
     * @return past Date minus i days
     */
    private Date yesterday(int i) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -i);
        return cal.getTime();
    }
}
