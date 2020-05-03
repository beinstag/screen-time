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
        Locale loc = c.getResources().getConfiguration().locale;
        dateFormat = new SimpleDateFormat(DATE_FORMAT, loc);
        hourFormat = new SimpleDateFormat(HOUR_FORMAT, loc);
        datePreferences = c.getSharedPreferences("datePrefs", MODE_PRIVATE);
        hourPreferences = c.getSharedPreferences("hourPrefs", MODE_PRIVATE);
        sharedPreferences = c.getSharedPreferences("sharePrefs", MODE_PRIVATE);
    }

    /**
     * @param date date of the day
     * @param screenDuration duration of active screen of day
     */
    void saveDailyScreenDuration(String date, int screenDuration) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(date, screenDuration);
        editor.apply();
    }


    /**
     * Save screenDuration to Shared Preferences and saves current date as last saved date.
     */
    void saveScreenDuration(int screenDuration) {
        String date = dateFormat.format(new Date());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(date, screenDuration);
        editor.apply();

        SharedPreferences.Editor dateEditor = datePreferences.edit();
        editor.putString(DATE, date);
        dateEditor.apply();
    }

    /**
     * Returns last date app was used or today's date (if it's used for the first time)
     *
     * @return last date app was used or today's date
     */
    String loadDate() {
        return datePreferences.getString(DATE, dateFormat.format(new Date()));
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
    int loadScreenDuration() {
        return sharedPreferences.getInt(dateFormat.format(new Date()), 0);
    }

    /**
     * Returns whether date stored (last date DataManager was used) is different from date of day
     * @return true if current day is different from last day this DataManager was used
     */
    boolean isTodayNewDayOfData() {
        return !loadDate().equals(dateFormat.format(new Date()));
    }

    /**
     * Returns screenDuration (in seconds) of current time hour
     *
     * @return screenDuration (in seconds) of current time hour
     */
    int loadHourlyScreenDuration() {
        return hourPreferences.getInt(hourFormat.format(new Date()), 0);
    }

    /**
     * Returns whether current time hour is a new hour
     *
     * @return true if current time hour is different from last time hour this DataManager was used
     */
    boolean isNowNewHourOfData() {
        return !loadTime().equals(hourFormat.format(new Date()));
    }

    /**
     * Saves the passed screenDuration to hourly storage shared preferences
     *
     * @param hour           HH of the screenDuration to be saved
     * @param screenDuration hourly screen duration to be saved
     */
    void saveHourlyScreenDuration(String hour, int screenDuration) {
        SharedPreferences.Editor editor = hourPreferences.edit();
        editor.putInt(hour, screenDuration);
        editor.apply();
    }

    /**
     * Saves the passed screenDuration to current by-hour storage (shared preferences)
     *
     * @param screenDuration hourly screen duration to be saved
     */
    void saveHScreenDuration(int screenDuration) {
        SharedPreferences.Editor hourEditor = hourPreferences.edit();
        hourEditor.putInt(hourFormat.format(new Date()), screenDuration);
        hourEditor.apply();

        SharedPreferences.Editor hEditor = datePreferences.edit();
        hEditor.putString(HOUR, hourFormat.format(new Date()));
        hEditor.apply();
    }

    /**
     * Returns hour HH when this Data Manager was last used or current HH if used for the first time
     *
     * @return hour HH when this Data Manager was last used
     */
    String loadTime() {
        return datePreferences.getString(HOUR, hourFormat.format(new Date()));
    }

    /*int getDaysCount() {
        return sharedPreferences.getAll().size();
    }

    int getHoursCount() {
         return hourPreferences.getAll().size();
    }*/

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




/*
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


    private void saveHourlyTestData(SharedPreferences.Editor editor) {
        editor.putInt(hourFormat.format(pastHour(1)), 1000);
        editor.putInt(hourFormat.format(pastHour(2)), 2000);
        editor.putInt(hourFormat.format(pastHour(3)), 3000);
        editor.putInt(hourFormat.format(pastHour(4)), 4000);
        editor.putInt(hourFormat.format(pastHour(5)), 5000);
        editor.putInt(hourFormat.format(pastHour(6)), 6000);
        editor.putInt(hourFormat.format(pastHour(7)), 7000);
        editor.putInt(hourFormat.format(pastHour(8)), 8000);
        editor.putInt(hourFormat.format(pastHour(9)), 9000);
        editor.putInt(hourFormat.format(pastHour(10)), 10000);
        editor.putInt(hourFormat.format(pastHour(11)), 11000);
        editor.putInt(hourFormat.format(pastHour(12)), 12000);
        editor.putInt(hourFormat.format(pastHour(13)), 13000);
        editor.putInt(hourFormat.format(pastHour(14)), 14000);
        editor.putInt(hourFormat.format(pastHour(15)), 15000);
        editor.putInt(hourFormat.format(pastHour(16)), 16000);
        editor.putInt(hourFormat.format(pastHour(17)), 17000);
        editor.putInt(hourFormat.format(pastHour(18)), 18000);
        editor.putInt(hourFormat.format(pastHour(19)), 19000);
        editor.putInt(hourFormat.format(pastHour(20)), 20000);
        editor.putInt(hourFormat.format(pastHour(21)), 21000);
        editor.putInt(hourFormat.format(pastHour(22)), 22000);
        editor.putInt(hourFormat.format(pastHour(23)), 23000);
        editor.apply();
    }*/

}
