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
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("Registered")
public class MyService extends Service {

    private final int NOTIFICATION_ID = 1;
    private int screenDuration;

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;

    private static Timer timer;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String CHANNEL_ID = "MyServiceChannel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
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

        //creates a timer which will increment the duration each seconds that the screen is interactive
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {

                    PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
                    if (pm != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                        if (pm.isInteractive()) { // If screen is active
                            DataManager dataManager = new DataManager(getApplicationContext());
                            screenDuration = dataManager.loadScreenDuration();
                            if (!dataManager.isTodayNewDayOfData()) { // If it's not a new day
                                screenDuration += 1; // Adds one to the screen duration
                            } else {
                                dataManager.saveDailyScreenDuration(dataManager.loadDate(), screenDuration);
                                screenDuration = 0; // Restarts on a new day (after midnight)
                            }
                        }
                    }

                    // Parses screenDuration to readable duration sDuration "00 hours 00 minutes 00 seconds"
                    String sDuration = new DurationParser(getApplicationContext()).parseToTextFormat(screenDuration);

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
                    DataManager dataManager = new DataManager(getApplicationContext());
                    dataManager.saveScreenDuration(screenDuration);
                    updateWidget();
                }
            }, 0, 1000);// in milliseconds
        }
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

}
