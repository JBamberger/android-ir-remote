package de.jbamberger.irremote.util

import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets

object IOUtils {

    @Throws(IOException::class)
    fun readString(inputStream: InputStream): String {
        return String(inputStream.readBytes(), StandardCharsets.UTF_8)
    }
}