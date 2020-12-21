package de.jbamberger.irremote.util

import de.jbamberger.irremote.ir.CodeTranslator
import de.jbamberger.irremote.ir.IrCodeFormat.valueOf
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
class RemoteParser {
    data class RemoteDefinition(val layout: LayoutDef, val commandDefs: IrDef) {
        constructor(obj: JSONObject) : this(
                LayoutDef(obj.getJSONObject("layout")),
                IrDef(obj.getJSONObject("irDef"))
        )
    }

    data class IrDef(val frequency: Int, val codeMap: Map<String, IntArray>) {
        constructor(obj: JSONObject) : this(
                frequency = obj.getInt("frequency"),
                codeMap = buildCodeMap(
                        obj.getString("codeFormat"),
                        obj.getJSONObject("codeMap")
                )
        )

        companion object {
            private fun buildCodeMap(
                    codeFormat: String, codeMap: JSONObject): Map<String, IntArray> {
                val format = valueOf(codeFormat.toUpperCase(Locale.ROOT))
                val translator = CodeTranslator.getTranslator(format)

                return codeMap.toStringMap().mapValues { translator.buildCode(it.value) }
            }
        }
    }

    data class LayoutDef(val width: Int, val height: Int, val controls: Array<Array<Control?>>) {

        init {
            check(height == controls.size) { "Expected size $height but got ${controls.size}" }
        }

        constructor(obj: JSONObject) : this(
                obj.getInt("width"),
                obj.getInt("height"),
                obj.getJSONArray("elements").map<JSONArray, Array<Control?>> { row ->
                    row.map<JSONObject?, Control?> { if (it == null) null else Control(it) }
                }
        )

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is LayoutDef) return false

            if (width != other.width) return false
            if (height != other.height) return false
            if (!controls.contentDeepEquals(other.controls)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = width
            result = 31 * result + height
            result = 31 * result + controls.contentDeepHashCode()
            return result
        }
    }

    data class Control(val name: String, val command: String) {
        constructor(obj: JSONObject) : this(obj.getString("name"), obj.getString("command"))
    }

    fun parse(s: String): Map<String, RemoteDefinition> {
        val remoteMap: MutableMap<String, RemoteDefinition> = HashMap()
        val remotes = JSONObject(s)
        val names = remotes.names() ?: return emptyMap()
        names.forEachString {
            remoteMap[it] = RemoteDefinition(remotes.getJSONObject(it))
        }
        return remoteMap
    }

    companion object {
        private inline fun JSONArray.forEachString(operation: (String) -> Unit) {
            for (i in 0 until length()) {
                operation.invoke(this.getString(i))
            }
        }

        private inline fun <reified MAP_FROM, reified MAP_TO> JSONArray.map(
                f: (MAP_FROM) -> MAP_TO) = Array(length()) { f.invoke(this.opt(it) as MAP_FROM) }

        private fun JSONObject.toStringMap(): Map<String, String> {
            val map: MutableMap<String, String> = HashMap(length())
            this.keys().forEach { map[it] = this.getString(it) }
            return map
        }
    }
}



