package de.jbamberger.irremote.ir

import de.jbamberger.irremote.util.HexUtils

object PanasonicProtocol : IRProtocol {
    override fun translate(data: ByteArray): IntArray {
        val code = IntArray(3 + data.size * 16) // start + end + data
        var c = 0

        // start sequence
        code[c++] = 3456
        code[c++] = 1728

        // data bytes
        for (b in data) {
            for (j in 0..7) {
                code[c++] = 432
                code[c++] = when (b.toInt() and (128 shr j)) {
                    0 -> 432
                    else -> 1296
                }
            }
        }

        // end sequence
        code[c] = 432

        return code
    }

    override fun carrierFrequency(): Int = 37000
}