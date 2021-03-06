package com.github.beinstag.screentime;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressLint("Registered")
public class MyService extends Service {
    private static AtomicInteger activitiesLaunched = new AtomicInteger(0);
    private final int NOTIFICATION_ID = 1;
    private static final String TAG = "MyService";
    int screenDailyDuration, screenHourlyDuration;
    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private DataManager dataManager;
    private static Timer timer;

    @Override
    public void onCreate() {

        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String CHANNEL_ID = "MyServiceChannel";

        if (notificationManager != null) {
            CharSequence name = "PersonalNotifications";
            String description = "Include all personal notifications.";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationChannel.setDescription(description);
            assert (notificationManager != null);

            notificationManager.createNotificationChannel(notificationChannel);
        }
        Intent notificationIntent = new Intent(this, HistoryActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        Notification notification = notificationBuilder
                .setContentTitle(getString(R.string.notificationTitle))
                .setContentText(getString(R.string.timespent))
                .setSmallIcon(R.drawable.ic_screentime_notif)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(NOTIFICATION_ID, notification);
    }


    private void updateWidget() {
        Intent intent = new Intent(this, SimpleWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), SimpleWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (activitiesLaunched.incrementAndGet() <= 1) {
            dataManager = new DataManager(getApplicationContext());
            screenDailyDuration = dataManager.loadLastScreenDuration();
            screenHourlyDuration = dataManager.loadLastHourlyScreenDuration();
            if (timer == null) {
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Date now = new Date();
                        computeScreenDuration(now);
                        updateNotification(now);
                        updateWidget();
                    }
                }, 0, 1000);// in milliseconds
            }

            return START_STICKY;
        } return START_REDELIVER_INTENT;
    }

    boolean isDisplayON() {

        DisplayManager dm = (DisplayManager)
                getSystemService(DISPLAY_SERVICE);
        if (dm != null) for (Display display : dm.getDisplays()) {

            if (display.getState() != Display.STATE_OFF) {
                return true;
            }
        }
        return false;
    }

    void computeScreenDuration(Date now) {
        Log.d(TAG, now.toString());
        if (isDisplayON()) { // If screen is active
            boolean isNewDay = dataManager.isNewDayOfData(now);
            boolean isNewHour = dataManager.isNewHourOfData(now);
            if (isNewDay) { // For a new day retrieve, save, reset both past day & hour durations
                Log.d(TAG, "isNewDay");
                screenDailyDuration = 0;
                screenHourlyDuration = 0;
            } else {
                if (isNewHour) { // For a new hour retrieve, save & reset past hour duration
                    Log.d(TAG, "isNewHour");
                    screenDailyDuration++;
                    screenHourlyDuration = 0;
                } else { // If it's not a new hour
                    screenDailyDuration++;
                    screenHourlyDuration++;
                }
            }
            dataManager.saveScreenDuration(screenDailyDuration, now);
            dataManager.saveHScreenDuration(screenHourlyDuration, now);
            Log.d(TAG, screenDailyDuration + " " + screenHourlyDuration);
        }
    }

    void updateNotification(Date now) {
        // Parses screenDuration to readable duration sDuration "00 hours 00 minutes 00 seconds"
        String sDuration = new DurationParser(getApplicationContext()).parseToTextFormat(dataManager.loadScreenDuration(now));
        // Create an Intent for the activity you want to start
        Intent intent = new Intent(getApplicationContext(), HistoryActivity.class);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addNextIntentWithParentStack(intent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder.setContentTitle(getString(R.string.notificationTitle))
                .setSmallIcon(R.drawable.ic_screentime_notif)
                .setContentIntent(resultPendingIntent) //will start intent on click
                .setContentText(getString(R.string.timespent)
                        + sDuration
                        + getString(R.string.onyourphone));

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null)
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    @Override
    public void onDestroy() {
        //remove this activity from the counter
        activitiesLaunched.getAndDecrement();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
