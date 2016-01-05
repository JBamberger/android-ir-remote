package de.jbapps.ledirremote;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutostartReceiver extends BroadcastReceiver {
    public AutostartReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, RemoteNotificationService.class));
    }
}
