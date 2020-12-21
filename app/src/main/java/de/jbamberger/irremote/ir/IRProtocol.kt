package de.jbamberger.irremote.ir

interface IRProtocol {
    /**
     * Translates the input sequence into an array of ON and OFF times.
     */
    fun translate(data: ByteArray): IntArray
    fun carrierFrequency(): Int
}