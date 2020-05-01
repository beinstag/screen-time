package com.github.beinstag.screentime;

import android.content.Context;

import java.util.Locale;

class DurationParser {
    private Context context;
    private String parsedDuration = "";

    DurationParser(Context context) {
        this.context = context;
    }


    String parseToTimeFormat(int duration) {
        double minutes = Math.floor((double) duration / 60);
        double hours = Math.floor(minutes / 60);
        double days = Math.floor(hours / 24);
        if(days > 0)
            parsedDuration += String.format(Locale.FRANCE, "%d", (int) days) + "d";
        if (hours > 0)
            parsedDuration += String.format(Locale.FRANCE, "%02d", (int) hours % 24) + ":";
        if (minutes > 0)
            parsedDuration += String.format(Locale.FRANCE, "%02d", (int) minutes % 60) + ":";
        parsedDuration += String.format(Locale.FRANCE, "%02d", duration % 60);
        return parsedDuration;
    }

    String parseToTextFormat(int duration) {
        double minutes = Math.floor((double) duration / 60);
        double hours = Math.floor(minutes / 60);
        double days = Math.floor(hours / 24);
        if(days > 0)
            parsedDuration += String.format(Locale.FRANCE, "%d", (int) days) + context.getString(R.string.days);
        if (hours > 0)
            parsedDuration += String.format(Locale.FRANCE, "%02d", (int) hours % 24) + context.getString(R.string.hours);
        if (minutes > 0)
            parsedDuration += String.format(Locale.FRANCE, "%02d", (int) minutes % 60) + context.getString(R.string.minutes);
        parsedDuration += String.format(Locale.FRANCE, "%02d", duration % 60) + context.getString(R.string.seconds);
        return parsedDuration;
    }
}
