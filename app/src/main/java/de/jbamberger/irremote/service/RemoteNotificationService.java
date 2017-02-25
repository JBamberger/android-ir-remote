package de.jbamberger.irremote.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.hardware.ConsumerIrManager;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import de.jbamberger.irremote.R;
import de.jbamberger.irremote.util.Tools;


public class RemoteNotificationService extends Service {

    private static final String TAG = "REMOTESERVICE";

    public static final int COM_SEND_CODE = 5;

    private BroadcastReceiver mReceiver;
    private Messenger mServer = new Messenger(new IncomingHandler());
    private ConsumerIrManager mIRManager;

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
            Log.d(TAG, "message incoming: " + msg.what);
            switch (msg.what) {
                case COM_SEND_CODE:
                    Log.d(TAG, "Received " + msg.arg1);
                    send(msg.arg1);

                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setForegroundPriority();
        mIRManager = (ConsumerIrManager) getSystemService(CONSUMER_IR_SERVICE);
        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Received message");
                int code = intent.getIntExtra(IR_CODE_NAME, -100);
                Log.d(TAG, "Code: " + code);
                send(code);
            }
        };
        IntentFilter intentFilter = new IntentFilter(getString(R.string.intentfilter_send_code));
        registerReceiver(mReceiver, intentFilter);
        Log.d(TAG, "Receiver registered.");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        Log.d(TAG, "Receiver unregistered.");
    }

    private void send(int codeNumber) {
        if (mIRManager != null) {
            if (codeNumber >= 0 && codeNumber < IR_CODES.length) {
                mIRManager.transmit(FREQUENCY, IR_CODES[codeNumber]);
                Log.d(TAG, "Transmitted code nr. " + codeNumber + " at freq: " + FREQUENCY);
            }
        }
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
                        IR_CODE_POWER,
                        IR_CODE_PLAY,
                        IR_CODE_UP,
                        IR_CODE_DOWN
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
                    Tools.generateIRSenderIntent(getApplicationContext(), i[1]),
                    PendingIntent.FLAG_UPDATE_CURRENT));
        }
        this.startForeground(101, notification);
        Log.i("SERVICE", "Started with foreground priority");
    }


    private static final int FREQUENCY = 38000;
    private static final int[][] IR_CODES = {
            /*0  power    */{9000, 4500, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 1690, 560},
            /*1  play     */{9000, 4500, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 1690, 560},
            /*2  up       */{9000, 4500, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 1690, 560, 560, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 1690, 560},
            /*3  down     */{9000, 4500, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 1690, 560},
            /*4  quick    */{9000, 4500, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560},
            /*5  slow     */{9000, 4500, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560},
            /*6  auto     */{9000, 4500, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560},
            /*7  flash    */{9000, 4500, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560},
            /*8  jump3    */{9000, 4500, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560},
            /*9  jump7    */{9000, 4500, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560},
            /*10 fade3    */{9000, 4500, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560},
            /*11 fade7    */{9000, 4500, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560},
            /*12 redUp    */{9000, 4500, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 560, 560, 1690, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560},
            /*13 redDown  */{9000, 4500, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560},
            /*14 greenUp  */{9000, 4500, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 1690, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 1690, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560},
            /*15 greenDown*/{9000, 4500, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560},
            /*16 blueUp   */{9000, 4500, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560},
            /*17 blueDown */{9000, 4500, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 1690, 560, 1690, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560},
            /*18 diy1     */{9000, 4500, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560},
            /*19 diy2     */{9000, 4500, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560},
            /*20 diy3     */{9000, 4500, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560},
            /*21 diy4     */{9000, 4500, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560},
            /*22 diy5     */{9000, 4500, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560},
            /*23 diy6     */{9000, 4500, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 1690, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560},
            /*24 red0     */{9000, 4500, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 560, 560, 1690, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 1690, 560},
            /*25 red1     */{9000, 4500, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 1690, 560, 560, 560, 1690, 560, 560, 560, 1690, 560, 1690, 560, 560, 560, 1690, 560, 560, 560, 1690, 560, 560, 560, 1690, 560},
            /*26 red2     */{9000, 4500, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 1690, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 1690, 560, 560, 560, 1690, 560},
            /*27 red3     */{9000, 4500, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560},
            /*28 red4     */{9000, 4500, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560},
            /*29 green0   */{9000, 4500, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 1690, 560},
            /*30 green1   */{9000, 4500, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 1690, 560, 560, 560, 1690, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 1690, 560, 560, 560, 1690, 560, 560, 560, 1690, 560},
            /*31 green2   */{9000, 4500, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 1690, 560, 560, 560, 1690, 560},
            /*32 green3   */{9000, 4500, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560},
            /*33 green4   */{9000, 4500, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560},
            /*34 blue0    */{9000, 4500, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 1690, 560},
            /*35 blue1    */{9000, 4500, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 560, 560, 1690, 560, 1690, 560, 560, 560, 1690, 560},
            /*36 blue2    */{9000, 4500, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 560, 560, 1690, 560},
            /*37 blue3    */{9000, 4500, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560},
            /*38 blue4    */{9000, 4500, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560},
            /*39 white0   */{9000, 4500, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 1690, 560, 1690, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 1690, 560},
            /*40 white1   */{9000, 4500, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 1690, 560, 1690, 560, 560, 560, 1690, 560},
            /*41 white2   */{9000, 4500, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 560, 560, 1690, 560},
            /*42 white3   */{9000, 4500, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560},
            /*43 white4   */{9000, 4500, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 560, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560}};


    public static final int IR_CODE_POWER = 0;
    public static final int IR_CODE_PLAY = 1;
    public static final int IR_CODE_UP = 2;
    public static final int IR_CODE_DOWN = 3;
    public static final int IR_CODE_QUICK = 4;
    public static final int IR_CODE_SLOW = 5;
    public static final int IR_CODE_AUTO = 6;
    public static final int IR_CODE_FLASH = 7;
    public static final int IR_CODE_JUMP_3 = 8;
    public static final int IR_CODE_JUMP_7 = 9;
    public static final int IR_CODE_FADE_3 = 10;
    public static final int IR_CODE_FADE_7 = 11;
    public static final int IR_CODE_REDUP = 12;
    public static final int IR_CODE_REDDOWN = 13;
    public static final int IR_CODE_GREENUP = 14;
    public static final int IR_CODE_GREENDOWN = 15;
    public static final int IR_CODE_BLUEUP = 16;
    public static final int IR_CODE_BLUEDOWN = 17;
    public static final int IR_CODE_DIY_1 = 18;
    public static final int IR_CODE_DIY_2 = 19;
    public static final int IR_CODE_DIY_3 = 20;
    public static final int IR_CODE_DIY_4 = 21;
    public static final int IR_CODE_DIY_5 = 22;
    public static final int IR_CODE_DIY_6 = 23;
    public static final int IR_CODE_RED_0 = 24;
    public static final int IR_CODE_RED_1 = 25;
    public static final int IR_CODE_RED_2 = 26;
    public static final int IR_CODE_RED_3 = 27;
    public static final int IR_CODE_RED_4 = 28;
    public static final int IR_CODE_GREEN_0 = 29;
    public static final int IR_CODE_GREEN_1 = 30;
    public static final int IR_CODE_GREEN_2 = 31;
    public static final int IR_CODE_GREEN_3 = 32;
    public static final int IR_CODE_GREEN_4 = 33;
    public static final int IR_CODE_BLUE_0 = 34;
    public static final int IR_CODE_BLUE_1 = 35;
    public static final int IR_CODE_BLUE_2 = 36;
    public static final int IR_CODE_BLUE_3 = 37;
    public static final int IR_CODE_BLUE_4 = 38;
    public static final int IR_CODE_WHITE_0 = 39;
    public static final int IR_CODE_WHITE_1 = 40;
    public static final int IR_CODE_WHITE_2 = 41;
    public static final int IR_CODE_WHITE_3 = 42;
    public static final int IR_CODE_WHITE_4 = 43;


    public static final String IR_CODE_NAME = "ir_code_name";
    public static final String IR_CODE_NAME_POWER = "power";
    public static final String IR_CODE_NAME_PLAY = "play";
    public static final String IR_CODE_NAME_UP = "up";
    public static final String IR_CODE_NAME_DOWN = "down";
    public static final String IR_CODE_NAME_QUICK = "quick";
    public static final String IR_CODE_NAME_SLOW = "slow";
    public static final String IR_CODE_NAME_AUTO = "auto";
    public static final String IR_CODE_NAME_FLASH = "flash";
    public static final String IR_CODE_NAME_JUMP_3 = "jump3";
    public static final String IR_CODE_NAME_JUMP_7 = "jump7";
    public static final String IR_CODE_NAME_FADE_3 = "fade3";
    public static final String IR_CODE_NAME_FADE_7 = "fade7";
    public static final String IR_CODE_NAME_REDUP = "redUp";
    public static final String IR_CODE_NAME_REDDOWN = "redDown";
    public static final String IR_CODE_NAME_GREENUP = "greenUp";
    public static final String IR_CODE_NAME_GREENDOWN = "greenDown";
    public static final String IR_CODE_NAME_BLUEUP = "blueUp";
    public static final String IR_CODE_NAME_BLUEDOWN = "blueDown";
    public static final String IR_CODE_NAME_DIY_1 = "diy1";
    public static final String IR_CODE_NAME_DIY_2 = "diy2";
    public static final String IR_CODE_NAME_DIY_3 = "diy3";
    public static final String IR_CODE_NAME_DIY_4 = "diy4";
    public static final String IR_CODE_NAME_DIY_5 = "diy5";
    public static final String IR_CODE_NAME_DIY_6 = "diy6";
    public static final String IR_CODE_NAME_RED_0 = "red0";
    public static final String IR_CODE_NAME_RED_1 = "red1";
    public static final String IR_CODE_NAME_RED_2 = "red2";
    public static final String IR_CODE_NAME_RED_3 = "red3";
    public static final String IR_CODE_NAME_RED_4 = "red4";
    public static final String IR_CODE_NAME_GREEN_0 = "green0";
    public static final String IR_CODE_NAME_GREEN_1 = "green1";
    public static final String IR_CODE_NAME_GREEN_2 = "green2";
    public static final String IR_CODE_NAME_GREEN_3 = "green3";
    public static final String IR_CODE_NAME_GREEN_4 = "green4";
    public static final String IR_CODE_NAME_BLUE_0 = "blue0";
    public static final String IR_CODE_NAME_BLUE_1 = "blue1";
    public static final String IR_CODE_NAME_BLUE_2 = "blue2";
    public static final String IR_CODE_NAME_BLUE_3 = "blue3";
    public static final String IR_CODE_NAME_BLUE_4 = "blue4";
    public static final String IR_CODE_NAME_WHITE_0 = "white0";
    public static final String IR_CODE_NAME_WHITE_1 = "white1";
    public static final String IR_CODE_NAME_WHITE_2 = "white2";
    public static final String IR_CODE_NAME_WHITE_3 = "white3";
    public static final String IR_CODE_NAME_WHITE_4 = "white4";


}
