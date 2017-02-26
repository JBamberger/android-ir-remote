package de.jbamberger.irremote.util;

import android.content.Context;

import java.io.IOException;

import de.jbamberger.irremote.R;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

public class LEDRemote44Key extends Remote {

    private final NECTranslator translator;

    public LEDRemote44Key(Context context) throws IOException{
        super(context, R.raw.led_remote_44_key);
        translator = new NECTranslator();
    }

    @Override
    int[] buildCode(String codeString) {
        return translator.getCode(codeString);
    }

    @Override
    int getFrequency() {
        return translator.getFrequency();
    }
}