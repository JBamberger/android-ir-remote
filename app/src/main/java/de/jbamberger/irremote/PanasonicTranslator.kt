package de.jbamberger.irremote

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

internal class PanasonicTranslator : CodeTranslator(
        initSequence = intArrayOf(3456, 1728),
        endSequence = intArrayOf(432),
        zero = intArrayOf(432, 432),
        one = intArrayOf(432, 1296),
        frequency = 37000) {

    override fun buildCode(codeString: String) = buildRawCode(hexToBytes(codeString))
}
