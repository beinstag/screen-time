package com.example.screentimer;

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
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("Registered")
public class MyService extends Service {
    private final String CHANNEL_ID = "personal_notifications";
    private final String SHARED_PREFS = "sharePrefs";
    private final String TIME = "time";
    private final String DATE = "date";
    private final String DATE_FORMAT = "dd/MM/yyyy";
    private final int NOTIFICATION_ID = 1;

    private NotificationManager notificationManager;
    private static Timer timer;

    private double screenDuration;
    private NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
    private String sDate;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service")
                .setContentText("input")
                .setSmallIcon(R.drawable.ic_time)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        //do heavy work on a background thread
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                    SimpleDateFormat formatter = new java.text.SimpleDateFormat(DATE_FORMAT, Locale.FRANCE);
                    screenDuration = sharedPreferences.getInt(TIME, 0);
                    sDate = sharedPreferences.getString(DATE, formatter.format(new Date()));
                    PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
                    if (pm != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                        if (pm.isInteractive()) { // If screen is active
                            if (sDate.equals(formatter.format(new Date()))) { // If it's not a new day
                                screenDuration += 1; // Adds one to the screen duration
                            } else {
                                sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putInt(sDate, (int) screenDuration);
                                editor.apply();
                                screenDuration = 0; // Restarts on a new day (after midnight)
                            }
                        }
                    }

                    // Parses screenDuration to readable duration sDuration "00 hours 00 minutes 00 seconds"
                    double minutes = Math.floor(screenDuration / 60);
                    double hours = Math.floor(minutes / 60);
                    String sDuration = "";
                    if (hours > 0)
                        sDuration = sDuration.concat("" + (int) hours % 24 + getString(R.string.hours));
                    if (minutes > 0)
                        sDuration = sDuration.concat("" + (int) minutes % 60 + getString(R.string.minutes));
                    sDuration += (int) screenDuration % 60 + getString(R.string.seconds);

                    // Create an Intent for the activity you want to start
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    // Create the TaskStackBuilder and add the intent, which inflates the back stack
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
                    stackBuilder.addNextIntentWithParentStack(intent);
                    // Get the PendingIntent containing the entire back stack
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                    notificationBuilder.setContentTitle(getString(R.string.notificationTitle))
                            .setSmallIcon(R.drawable.ic_time)
                            .setContentIntent(resultPendingIntent) //will start intent on click
                            .setContentText(getString(R.string.timespent)
                                    + sDuration
                                    + getString(R.string.onyourphone));

                    notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    if (notificationManager != null)
                        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
                    sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    formatter = new java.text.SimpleDateFormat(DATE_FORMAT, Locale.FRANCE);
                    editor.putInt(TIME, (int) screenDuration);
                    editor.putString(DATE, formatter.format(new Date()));
                    editor.apply();
                    updateWidget();
                }
            }, 0, 1000);// in milliseconds
        }
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
        String input = intent.getStringExtra("inputExtra");


        //stopSelf();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            if (notificationManager != null) {
                CharSequence name = "PersonalNotifications";
                String description = "Include all personal notifications.";
                int importance = NotificationManager.IMPORTANCE_LOW;
                NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                notificationChannel.setDescription(description);
                notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                assert (notificationManager != null);
                notificationManager.createNotificationChannel(notificationChannel);
            }
    }
}
