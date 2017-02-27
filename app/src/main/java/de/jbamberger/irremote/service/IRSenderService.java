package de.jbamberger.irremote.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.ConsumerIrManager;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.widget.Toast;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;
import java.util.TreeMap;

import de.jbamberger.irremote.util.LEDRemote44Key;
import de.jbamberger.irremote.util.PanasonicRemote;
import de.jbamberger.irremote.util.Remote;

public class IRSenderService extends IntentService {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LED_REMOTE_44_KEY, PANASONIC_REMOTE})
    public @interface RemoteType {
    }


    public static final int LED_REMOTE_44_KEY = 0;
    public static final int PANASONIC_REMOTE = 1;

    private static final String ACTION_SEND_IRCODE = "de.jbamberger.irremote.service.action.SEND_IRCODE";

    private static final String EXTRA_COMMAND_NAME = "de.jbamberger.irremote.service.extra.EXTRA_COMMAND_NAME";
    private static final String EXTRA_REMOTE_TYPE = "de.jbamberger.irremote.service.extra.EXTRA_REMOTE_TYPE";

    public IRSenderService() {
        super("IRSenderService");
    }


    private ConsumerIrManager mIRManager;
    private final Map<Integer, Remote> remotes = new TreeMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
        mIRManager = (ConsumerIrManager) getSystemService(CONSUMER_IR_SERVICE);
    }

    public static void startActionSendIrcode(Context context, int remoteType, String commandName) {
        Intent intent = new Intent(context, IRSenderService.class);
        intent.setAction(ACTION_SEND_IRCODE);
        intent.putExtra(EXTRA_COMMAND_NAME, remoteType);
        intent.putExtra(EXTRA_REMOTE_TYPE, commandName);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SEND_IRCODE.equals(action)) {
                final int remoteType = intent.getIntExtra(EXTRA_COMMAND_NAME, 0);
                final String commandName = intent.getStringExtra(EXTRA_REMOTE_TYPE);
                handleActionSendIrcode(remoteType, commandName);
            }
        }
    }


    private void handleActionSendIrcode(int remotetype, String name) {
        Remote remote = remotes.get(remotetype);
        if (remote == null) {
            try {
                switch (remotetype) {
                    case LED_REMOTE_44_KEY:
                        remote = new LEDRemote44Key(getApplicationContext());
                        break;
                    case PANASONIC_REMOTE:
                        remote = new PanasonicRemote(getApplicationContext());
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "Invalid Remote type.", Toast.LENGTH_LONG).show();
                        return;
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "IO Error while reading remote code.", Toast.LENGTH_LONG).show();
                return;
            }
            remotes.put(remotetype, remote);
        }

        int[] code = remote.getIRSequence(name);
        if (code != null) {
            send(remote.getFrequency(), code);
        } else {
            Toast.makeText(getApplicationContext(), "The selected remote doesn't support this command.", Toast.LENGTH_LONG).show();
        }
    }

    private void send(int frequency, @NonNull int[] code) {
        if (mIRManager != null && mIRManager.hasIrEmitter()) {
            ConsumerIrManager.CarrierFrequencyRange[] range = mIRManager.getCarrierFrequencies();
            for (ConsumerIrManager.CarrierFrequencyRange carrierFrequencyRange : range) {
                if (carrierFrequencyRange.getMinFrequency() <= frequency && frequency <= carrierFrequencyRange.getMaxFrequency()) {
                    mIRManager.transmit(frequency, code);
                    return;
                }
            }
            Toast.makeText(this, "The required frequency range is not supported.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "An error occurred while transmitting.", Toast.LENGTH_LONG).show();// TODO: 16.02.2017 replace with res
        }
    }
}
