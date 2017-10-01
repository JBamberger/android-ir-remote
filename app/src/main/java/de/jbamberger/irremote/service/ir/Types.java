package de.jbamberger.irremote.service.ir;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

public interface Types {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LED_REMOTE_44_KEY, PANASONIC_REMOTE})
    @interface RemoteType {
    }


    int LED_REMOTE_44_KEY = 0;
    int PANASONIC_REMOTE = 1;
}
