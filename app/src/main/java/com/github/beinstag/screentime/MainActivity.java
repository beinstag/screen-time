package com.github.beinstag.screentime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.ContextCompat;

import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends Activity {

    private static AtomicInteger activitiesLaunched = new AtomicInteger(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (activitiesLaunched.incrementAndGet() > 1) {
            finish();
        } else {
            Intent intent = new Intent(this, MyService.class);
            ContextCompat.startForegroundService(this, intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        //remove this activity from the counter
        activitiesLaunched.getAndDecrement();
        super.onDestroy();
    }
}
