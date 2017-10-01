package de.jbamberger.irremote.service.ir;

import android.content.Context;

import java.io.IOException;

import de.jbamberger.irremote.R;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

class LEDRemote44Key extends Remote {

    LEDRemote44Key(Context context) throws IOException{
        super(context, R.raw.led_remote_44_key, new NECTranslator());
    }
}
