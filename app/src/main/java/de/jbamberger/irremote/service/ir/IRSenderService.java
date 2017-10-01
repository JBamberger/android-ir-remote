package de.jbamberger.irremote.service.ir;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.ConsumerIrManager;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import timber.log.Timber;


public class IRSenderService extends IntentService {

    private static final String ACTION_SEND_IR_CODE = "de.jbamberger.irremote.service.action.SEND_IR_CODE";

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

    public static void startActionSendIrCode(Context context, @Remotes.RemoteType int remoteType, String commandName) {
        Intent intent = new Intent(context, IRSenderService.class);
        intent.setAction(ACTION_SEND_IR_CODE);
        intent.putExtra(EXTRA_COMMAND_NAME, remoteType);
        intent.putExtra(EXTRA_REMOTE_TYPE, commandName);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SEND_IR_CODE.equals(action)) {
                final int remoteType = intent.getIntExtra(EXTRA_COMMAND_NAME, 0);
                final String commandName = intent.getStringExtra(EXTRA_REMOTE_TYPE);
                handleActionSendIrCode(remoteType, commandName);
            }
        }
    }


    private void handleActionSendIrCode(@Remotes.RemoteType int remoteType, String name) {
        Remote remote = remotes.get(remoteType);
        if (remote == null) {
            try {
                remote = Remotes.getRemoteFromType(getApplicationContext(), remoteType);
            } catch (IOException e) {
                Timber.e(e,"IO Error while reading remote code.");
                return;
            }
            remotes.put(remoteType, remote);
        }

        int[] code = remote.getIRSequence(name);
        if (code != null) {
            send(remote.getFrequency(), code);
        } else {
            Timber.d("The selected remote doesn't support this command.");
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
            Timber.d("The required frequency range is not supported.");
        } else {
            Timber.d("An error occurred while transmitting.");// TODO: 16.02.2017 replace with res
        }
    }
}
