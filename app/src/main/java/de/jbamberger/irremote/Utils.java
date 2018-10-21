package de.jbamberger.irremote;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
public final class Utils {

    public static String readString(InputStream is) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[2048];
        int len;
        while ((len = is.read(buf)) != -1) {
            out.write(buf, 0, len);
        }
        return out.toString(StandardCharsets.UTF_8.name());
    }
}
