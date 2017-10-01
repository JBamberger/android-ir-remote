package de.jbamberger.irremote.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import de.jbamberger.irremote.R;
import de.jbamberger.irremote.service.ir.IRSenderService;
import de.jbamberger.irremote.service.ir.IrTools;
import timber.log.Timber;

import static de.jbamberger.irremote.service.ir.Remotes.LED_REMOTE_44_KEY;


public class RemoteNotificationService extends Service {

    public static final int COM_SEND_CODE = 5;
    public static final String ACTION_SEND_IR = "de.jbamberger.irremote.service.ACTION_SEND_IR";

    private BroadcastReceiver mReceiver;
    private Messenger mServer = new Messenger(new IncomingHandler());

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mServer.getBinder();
    }

    private class IncomingHandler extends android.os.Handler {
        @Override
        public void handleMessage(Message msg) {
            Timber.d("message incoming: " + msg.what);
            switch (msg.what) {
                case COM_SEND_CODE:
                    Timber.d("Received " + msg.arg1);
                    IRSenderService.startActionSendIrCode(getApplicationContext(), LED_REMOTE_44_KEY, "");//FIXME: msg.arg1);

                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setForegroundPriority();
        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                Timber.d("Received message");
                int code = intent.getIntExtra("ir_code_name" /*FIXME: IR_CODE_NAME*/, -100);
                Timber.d("Code: " + code);
                IRSenderService.startActionSendIrCode(getApplicationContext(), LED_REMOTE_44_KEY, "");//FIXME: code);
            }
        };
        IntentFilter intentFilter = new IntentFilter(ACTION_SEND_IR);
        registerReceiver(mReceiver, intentFilter);
        Timber.d("Receiver registered.");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        Timber.d("Receiver unregistered.");
    }

    private void setForegroundPriority() {
        int[][] intents = {
                {
                        R.id.button_power,
                        R.id.button_play,
                        R.id.button_brightness_up,
                        R.id.button_brightness_down
                },
                {
                        0, 0, 0, 0//IR_CODE_POWER,IR_CODE_PLAY,IR_CODE_UP,IR_CODE_DOWN
                }
        };

        Resources res = this.getResources();
        RemoteViews layout = new RemoteViews(getPackageName(), R.layout.notification_control);
        //RemoteViews layoutBig = new RemoteViews(getPackageName(), R.layout.notification_control_big);
        //new LEDRemoteUIInflater().generateLargeNotificationLayout(this, layoutBig);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                //TODO: foo .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_stat_notification)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContent(layout)
                .setAutoCancel(false);
        Notification notification = builder.build();
        //notification.bigContentView = layoutBig;
        for (int[] i : intents) {
            layout.setOnClickPendingIntent(i[0], PendingIntent.getBroadcast(this, i[0],
                    IrTools.generateIRSenderIntent(getApplicationContext(), i[1]),
                    PendingIntent.FLAG_UPDATE_CURRENT));
        }
        this.startForeground(101, notification);
        Timber.i("Started with foreground priority");
    }


}
