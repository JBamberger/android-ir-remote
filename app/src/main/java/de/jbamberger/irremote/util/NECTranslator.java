package de.jbamberger.irremote.util;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

public class NECTranslator extends CodeTranslationTool {

    public NECTranslator() {
        super(new int[]{9000, 4500}, new int[]{560}, new int[]{560, 560}, new int[]{560, 1690}, 38000);
    }

    @Override
    int[] getCode(String codeString) {
        return buildRawCode(injectInverse(hexToBytes(codeString)));
    }
}
