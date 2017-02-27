package de.jbamberger.irremote.util;

import android.content.Context;

import java.io.IOException;

import de.jbamberger.irremote.R;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

public class PanasonicRemote extends Remote {

    public PanasonicRemote(Context context) throws IOException {
        super(context, R.raw.led_remote_44_key, new PanasonicTranslator());
    }
}
