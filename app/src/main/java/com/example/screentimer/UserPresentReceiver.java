package com.example.screentimer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class UserPresentReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null) {
            String log = "Action: " + intent.getAction() + "\n" +
                    "URI: " + intent.toUri(Intent.URI_INTENT_SCHEME) + "\n";
            Log.d("UserPresentReceiver", log);
            Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
