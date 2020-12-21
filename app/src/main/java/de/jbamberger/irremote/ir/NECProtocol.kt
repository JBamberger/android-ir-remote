package de.jbamberger.irremote.ir

import kotlin.experimental.inv

object NECProtocol : IRProtocol {


    /**
     * The NEC protocol sends a start sequence, then each byte followed by the inverse of the byte
     * and finally the end sequence.
     */
    override fun translate(data: ByteArray): IntArray {

        // output size: start + end + data bytes + inverse data bytes
        val code = IntArray(3 + 2 * data.size * 16)
        var c = 0

        // start code
        code[c++] = 9000
        code[c++] = 4500

        // data bytes and data inverse
        for (inByte: Byte in data) {

            // insert a normal data byte
            for (j in 0..7) {
                code[c++] = 560
                code[c++] = when (inByte.toInt() and (128 shr j)) {
                    0 -> 560
                    else -> 1690
                }
            }

            // insert the inverted data byte
            val invByte = inByte.inv()
            for (j in 0..7) {
                code[c++] = 560
                code[c++] = when (invByte.toInt() and (128 shr j)) {
                    0 -> 560
                    else -> 1690
                }
            }
        }

        // end code
        code[c] = 560

        return code
    }

    override fun carrierFrequency(): Int = 38000
}