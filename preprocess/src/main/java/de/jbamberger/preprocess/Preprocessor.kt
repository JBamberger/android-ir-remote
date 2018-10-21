package de.jbamberger.preprocess

import java.io.File

class Preprocessor {
    fun main() {
        val map = File("keymap-gen/panasonic_map")
        val keyMap = map.readLines()
                .drop(1)
                .flatMap {
                    val key = it.substringBefore(";").trim()
                    val values = it.substringAfter(";")

                    values.split(",")
                            .asSequence()
                            .map(String::trim)
                            .map { it to key }
                            .toList()
                }.toMap()


        val input = File("keymap-gen/panasonic_preprocessed.csv")
        input.readLines()
                .asSequence()
                .drop(1)
                .map {
                    val x = it.split(",")
                    val num = x[0].trim()
                    val v = x.drop(3)
                            .dropLast(1)
                            .map(String::trim)
                            .chunked(2) {
                                val a = it[0]
                                val b = it[1]
                                when (a) {
                                    "432" -> {
                                        when (b) {
                                            "432" -> "0"
                                            "1296" -> "1"
                                            else -> "error"
                                        }
                                    }
                                    else -> "error"
                                }
                            }
                            .chunked(8) {
                                String.format("%02X", (
                                        (it[0].toInt() shl 7)
                                                or (it[1].toInt() shl 6)
                                                or (it[2].toInt() shl 5)
                                                or (it[3].toInt() shl 4)
                                                or (it[4].toInt() shl 3)
                                                or (it[5].toInt() shl 2)
                                                or (it[6].toInt() shl 1)
                                                or (it[7].toInt() shl 0)
                                        ).toByte())
                            }
                            .joinToString("")
                    Pair(num, v)
                }
                .chunked(3) { (a, b, c) ->
                    if (a.second == b.second && b.second == c.second) {
                        val k0 = keyMap[a.first]
                        val k1 = keyMap[b.first]
                        val k2 = keyMap[c.first]
                        if (k0 == k1 && k1 == k2) {
                        "\"$k0\":\"${a.second}\""
                        } else {
                            "Key mismatch"
                        }
                    } else {
                        "mismatch ${a.second}, ${b.second}, ${c.second}"
                    }
                }
                .forEach { println(it) }
    }
}
