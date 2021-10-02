/*
 *    Copyright 2021 Jannik Bamberger
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.jbamberger.irremote.remote

import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import kotlin.experimental.and

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
object Utils {

    @Throws(IOException::class)
    fun readString(inputStream: InputStream): String {
        return String(inputStream.readBytes(), StandardCharsets.UTF_8)
    }

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
