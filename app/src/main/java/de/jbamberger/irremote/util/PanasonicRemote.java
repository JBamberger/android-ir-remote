package de.jbamberger.irremote.util;

import android.content.Context;

import java.io.IOException;

import de.jbamberger.irremote.R;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

public class PanasonicRemote extends Remote {
    private final PanasonicTranslator translator;

    public PanasonicRemote(Context context) throws IOException {
        super(context, R.raw.led_remote_44_key);
        translator = new PanasonicTranslator();
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
