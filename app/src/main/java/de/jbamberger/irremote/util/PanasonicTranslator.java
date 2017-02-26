package de.jbamberger.irremote.util;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

public class PanasonicTranslator extends CodeTranslationTool {

    public PanasonicTranslator() {
        super(new int[]{3456, 1728}, new int[]{432}, new int[]{432, 432}, new int[]{432, 1296}, 37000);
    }

    @Override
    int[] getCode(String codeString) {
        return buildRawCode(hexToBytes(codeString));
    }
}
