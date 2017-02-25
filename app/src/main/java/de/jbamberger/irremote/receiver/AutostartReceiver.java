package de.jbamberger.irremote.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import de.jbamberger.irremote.service.RemoteNotificationService;

public class AutostartReceiver extends BroadcastReceiver {
    public AutostartReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, RemoteNotificationService.class));
    }
}
