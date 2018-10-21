package de.jbamberger.irremote.service.ir

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

internal class NECTranslator : CodeTranslator(
        initSequence = intArrayOf(9000, 4500),
        endSequence = intArrayOf(560),
        zero = intArrayOf(560, 560),
        one = intArrayOf(560, 1690),
        frequency = 38000) {

    override fun buildCode(codeString: String) =
            buildRawCode(injectInverse(hexToBytes(codeString)))

}
