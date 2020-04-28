package com.github.beinstag.screentime;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends Activity {

    private static AtomicInteger activitiesLaunched = new AtomicInteger(0);

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (activitiesLaunched.incrementAndGet() > 1) {
            finish();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent intent = new Intent(this, MyService.class);
                ContextCompat.startForegroundService(this, intent);
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        //remove this activity from the counter
        activitiesLaunched.getAndDecrement();
        super.onDestroy();
    }
}
