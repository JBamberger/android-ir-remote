package de.jbamberger.preprocess

import java.io.File

class Preprocessor {
    fun main() {
        val map = File("keymap-gen/panasonic_map")
        val keyMap = map.readLines()
                .drop(1)
                .flatMap { line ->
                    val key = line.substringBefore(";").trim()
                    val values = line.substringAfter(";")

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
                .map { line ->
                    val x = line.split(",")
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
                                var value = 0

                                for (i in 0..7) {
                                    value = value or (it[i].toInt() shl (7 - i))
                                }

                                String.format("%02X", value.toByte())
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
