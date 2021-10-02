package de.jbamberger.irremote.remote

import de.jbamberger.irremote.remote.Utils.hexToBytes
import java.util.*
import kotlin.experimental.inv

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

internal class CodeTranslator
/**
 * one and zero have to have the same length.
 *
 * @param initSequence Sequence transmitted only once in the beginning
 * @param endSequence  Sequence transmitted only once in the end
 * @param zero         Sequence representing a binary zero
 * @param one          Sequence representing a binary one
 */
private constructor(
        private val initSequence: IntArray,
        private val endSequence: IntArray,
        private val zero: IntArray,
        private val one: IntArray,
        private val frequency: Int,
        private val transform: (String) -> ByteArray) {

    /**
     * returns the on off sequence that is represented by the codeString
     *
     * @param codeString code
     * @return on off sequence of the codeString
     */
    fun buildCode(codeString: String) = buildRawCode(transform.invoke(codeString))


    /**
     * Creates an int array, writes the start sequence, then the bytes represented by one and zero
     * and finally the end sequence. TODO: endianness
     *
     * @param data the bytes to be sandwiched between start and end
     * @return the encoded sequence
     */
    private fun buildRawCode(data: ByteArray): IntArray {
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
        public enum class IrCodeFormat {
            NEC, PANASONIC
        }

        /**
         * This method injects the inverse of every byte into the array. {a, b} becomes {a, ~a, b, ~b}.
         *
         * @param data input data
         * @return data with injected inverses
         */
        private fun injectInverse(data: ByteArray): ByteArray {
            val res = ByteArray(data.size * 2)
            for (i in data.indices) {
                res[2 * i] = data[i]
                res[2 * i + 1] = data[i].inv()
            }
            return res
        }

        fun getTranslator(format: IrCodeFormat) = when (format) {
            IrCodeFormat.NEC -> getNecTranslator()
            IrCodeFormat.PANASONIC -> getPanasonicTranslator()
        }

        fun getTranslator(format: String) =
                getTranslator(IrCodeFormat.valueOf(format.uppercase(Locale.ROOT)))

        private fun getNecTranslator() = CodeTranslator(
                initSequence = intArrayOf(9000, 4500),
                endSequence = intArrayOf(560),
                zero = intArrayOf(560, 560),
                one = intArrayOf(560, 1690),
                frequency = 38000,
                transform = { injectInverse(hexToBytes(it)) })

        private fun getPanasonicTranslator() = CodeTranslator(
                initSequence = intArrayOf(3456, 1728),
                endSequence = intArrayOf(432),
                zero = intArrayOf(432, 432),
                one = intArrayOf(432, 1296),
                frequency = 37000,
                transform = Utils::hexToBytes)
    }
}
