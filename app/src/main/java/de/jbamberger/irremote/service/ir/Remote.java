package de.jbamberger.irremote.service.ir;

import android.content.Context;
import android.support.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

abstract class Remote {

    private final Map<String, int[]> codes = new TreeMap<>();
    private final CodeTranslator translator;

    Remote(Context context, int resId, CodeTranslator translator) throws IOException {
        this.translator = translator;
        List<String> codeList = readRemoteFile(context, resId);

        for (String code : codeList) {
            String[] parts = code.split(":");
            if (parts.length == 2) {
                codes.put(parts[0], buildCode(parts[1]));
            } else {
                throw new IOException("Couldn't split code: \"" + code + "\" into two parts.");
            }
        }
    }


    int getFrequency() {
        return translator.getFrequency();
    }

    @Nullable
    int[] getIRSequence(String name) {
        return codes.get(name);
    }

    public Set<String> getCommands() {
        return codes.keySet();
    }

    private int[] buildCode(String codeString) throws IOException {
        return translator.buildCode(codeString);
    }

    private static List<String> readRemoteFile(Context context, int resourceId) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        context.getResources().openRawResource(resourceId)));
        String line;
        List<String> codes = new ArrayList<>();

        while ((line = reader.readLine()) != null) {
            codes.add(line);
        }
        return codes;
    }
}
