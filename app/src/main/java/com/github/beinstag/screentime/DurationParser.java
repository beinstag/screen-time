package com.github.beinstag.screentime;

import android.content.Context;

class DurationParser {
    private Context context;
    private String parsedDuration = "";

    DurationParser(Context context) {
        this.context = context;
    }



    String parseToTimeFormat(int duration) {
        double minutes = Math.floor((double) duration / 60);
        double hours = Math.floor(minutes / 60);


        if (hours > 0) parsedDuration += (int) hours % 24 + ":";
        if (minutes > 0) parsedDuration += (int) minutes % 60 + ":";
        parsedDuration += (int) (double) duration % 60;
        return parsedDuration;
    }

    String parseToTextFormat(int duration){
        double minutes = Math.floor((double)duration / 60);
        double hours = Math.floor(minutes / 60);
        parsedDuration = "";
        if (hours > 0)
            parsedDuration = parsedDuration.concat("" + (int) hours % 24 + context.getString(R.string.hours));
        if (minutes > 0)
            parsedDuration = parsedDuration.concat("" + (int) minutes % 60 + context.getString(R.string.minutes));
        return parsedDuration += duration % 60 + context.getString(R.string.seconds);
    }
}
