package com.example.screentimer;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

public class SimpleWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        String SHARED_PREFS = "sharePrefs";
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS, 0);
        for (int widgetId : appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.simple_widget);
            String TIME = "time";
            double seconds = prefs.getInt(TIME, 0);
            double minutes = Math.floor(seconds / 60);
            double hours = Math.floor(minutes / 60);
            String sDuration = "";

            if (hours > 0)
                sDuration += (int) hours % 24 + ":";
            if (minutes > 0)
                sDuration += (int) minutes % 60 + ":";
            sDuration += (int) seconds % 60+"";

            remoteViews.setTextViewText(R.id.textView, sDuration);

            Intent intent = new Intent(context, HistoryActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            remoteViews.setOnClickPendingIntent(R.id.linearLayout, pendingIntent);

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }
}
