package de.jbamberger.irremote.service.ir

import kotlin.experimental.and
import kotlin.experimental.inv

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

internal abstract class CodeTranslator
/**
 * one and zero have to have the same length.
 *
 * @param initSequence Sequence transmitted only once in the beginning
 * @param endSequence  Sequence transmitted only once in the end
 * @param zero         Sequence representing a binary zero
 * @param one          Sequence representing a binary one
 */
(private val initSequence: IntArray, private val endSequence: IntArray,
 private val zero: IntArray, private val one: IntArray, val frequency: Int) {

    /**
     * returns the on off sequence that is represented by the codeString
     *
     * @param codeString code
     * @return on off sequence of the codeString
     */
    abstract fun buildCode(codeString: String): IntArray

    /**
     * This method injects the inverse of every byte into the array. {a, b} becomes {a, ~a. b, ~b}.
     *
     * @param data input data
     * @return data with injected inverses
     */
    fun injectInverse(data: ByteArray): ByteArray {
        val res = ByteArray(data.size * 2)
        for (i in data.indices) {
            res[2 * i] = data[i]
            res[2 * i + 1] = data[i].inv().toByte()
        }
        return res
    }

    /**
     * Creates an int array, writes the start sequence, then the bytes represented by one and zero
     * and finally the end sequence. TODO: endianness
     *
     * @param data the bytes to be sandwiched between start and end
     * @return the encoded sequence
     */
    fun buildRawCode(data: ByteArray): IntArray {
        val size = initSequence.size + data.size * (8 * zero.size) + endSequence.size
        val code = IntArray(size)
        System.arraycopy(initSequence, 0, code, 0, initSequence.size)
        var c = initSequence.size
        for (b in data) {
            for (j in 0..7) {
                if (b.toInt() and (128 shr j) == 0) {
                    System.arraycopy(zero, 0, code, c, zero.size)
                } else {
                    System.arraycopy(one, 0, code, c, one.size)
                }
                c += 2
            }
        }
        System.arraycopy(endSequence, 0, code, c, endSequence.size)
        return code
    }

    companion object {

        /**
         * Creates a byte representation of a hexadecimal string.
         *
         * @param s hex string
         * @return bytes encoded in the string
         */
        fun hexToBytes(s: String): ByteArray {
            val len = s.length
            val data = ByteArray(len / 2)
            var i = 0
            while (i < len) {
                data[i / 2] = ((Character.digit(s[i], 16) shl 4) + Character.digit(s[i + 1], 16)).toByte()
                i += 2
            }
            return data
        }

        private val hexArray = "0123456789ABCDEF".toCharArray()

        /**
         * encodes a byte sequence into a hex string.
         *
         * @param bytes input
         * @return bytes as hex string
         */
        fun bytesToHex(bytes: ByteArray): String {
            val hexChars = CharArray(bytes.size * 2)
            for (j in bytes.indices) {
                val v = bytes[j] and 0xFF.toByte()
                hexChars[j * 2] = hexArray[v.toInt() ushr 4]
                hexChars[j * 2 + 1] = hexArray[v.toInt() and 0x0F]
            }
            return String(hexChars)
        }
    }
}
