package de.jbamberger.irremote.util

import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import kotlin.experimental.and

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
object HexUtils {

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
