package de.jbamberger.irremote.service.ir;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import de.jbamberger.irremote.R;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

public final class Remotes {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LED_REMOTE_44_KEY, PANASONIC_REMOTE, CEIL_REMOTE})
    @interface RemoteType {
    }


    public static final int LED_REMOTE_44_KEY = 0;
    public static final int PANASONIC_REMOTE = 1;
    public static final int CEIL_REMOTE = 2;

    private Remotes() {
        throw new AssertionError("No instances of Remotes!");
    }

    @NonNull
    public static Remote getRemoteFromType(@NonNull Context context, @RemoteType int type) throws IOException {
        switch (type) {
            case LED_REMOTE_44_KEY:
                return getLed44KeyRemote(context);
            case PANASONIC_REMOTE:
                return getPanasonicRemote(context);
            case CEIL_REMOTE:
                return getCeilRemote(context);
            default:
                throw new IllegalArgumentException("Tried to get remote with invalid type: " + type);
        }
    }

    @NonNull
    private static Remote getLed44KeyRemote(@NonNull Context context) throws IOException {
        return new Remote(readRemoteFile(context, R.raw.led_remote_44_key), new NECTranslator());
    }

    @NonNull
    private static Remote getPanasonicRemote(@NonNull Context context) throws IOException {
        return new Remote(readRemoteFile(context, R.raw.panasonic_remote), new PanasonicTranslator());
    }

    @NonNull
    private static Remote getCeilRemote(@NonNull Context context) throws IOException {
        return new Remote(readRemoteFile(context, R.raw.remotes), new ExpandedNECTranslator());
    }

    private static List<String> readRemoteFile(Context context, int id) throws IOException {
        Resources r = context.getResources();
        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(r.openRawResource(id)))) {
            String line;
            List<String> codes = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                codes.add(line);
            }
            return codes;
        }
    }
}
