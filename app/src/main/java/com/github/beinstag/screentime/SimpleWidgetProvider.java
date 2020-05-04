package com.github.beinstag.screentime;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.util.Date;

public class SimpleWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        DataManager dataManager = new DataManager(context);
        int screenDuration = dataManager.loadScreenDuration(new Date());
        for (int widgetId : appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.simple_widget);

            String sDuration = new DurationParser(context).parseToTimeFormat(screenDuration);
            remoteViews.setTextViewText(R.id.textView, sDuration);
            remoteViews.setTextViewText(R.id.textView2, context.getString(R.string.app_name));
            Intent intent = new Intent(context, HistoryActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            remoteViews.setOnClickPendingIntent(R.id.linearLayout, pendingIntent);

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }
}
