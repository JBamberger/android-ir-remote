package de.jbamberger.irremote.remote

import org.json.JSONArray
import org.json.JSONObject
import java.util.*

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
class RemoteParser {

    fun parse(s: String): Map<String, RemoteDefinition> {
        val remoteMap: MutableMap<String, RemoteDefinition> = HashMap()
        val remotes = JSONObject(s)
        val names = remotes.names() ?: return emptyMap()
        names.forEachString { remoteMap[it] = RemoteDefinition.fromJson(remotes.getJSONObject(it)) }
        return remoteMap
    }

    companion object {
        private inline fun JSONArray.forEachString(operation: (String) -> Unit) {
            for (i in 0 until length()) {
                operation.invoke(getString(i))
            }
        }
    }
}


data class IrRemoteKey(val name: String, val command: String) {
    companion object {
        fun fromJson(obj: JSONObject) = IrRemoteKey(obj.getString("name"), obj.getString("command"))
    }
}

data class IrRemoteLayout(val width: Int, val height: Int, val keys: Array<Array<IrRemoteKey?>>) {

    init {
        if (height != keys.size) {
            throw IllegalStateException("Expected size $height but got ${keys.size}")
        }
    }

    companion object {
        fun fromJson(obj: JSONObject) = IrRemoteLayout(
                obj.getInt("width"),
                obj.getInt("height"),
                obj.getJSONArray("elements")
                        .mapArr { row -> row.mapObj { it?.run { IrRemoteKey.fromJson(this) } } }
        )

        private inline fun <reified T> JSONArray.mapArr(operation: (JSONArray) -> T) =
                Array(length()) { operation.invoke(getJSONArray(it)) }

        private inline fun <reified T> JSONArray.mapObj(operation: (JSONObject?) -> T) =
                Array(length()) { operation.invoke(optJSONObject(it)) }

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is IrRemoteLayout) return false

        if (width != other.width) return false
        if (height != other.height) return false
        if (!keys.contentDeepEquals(other.keys)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = width
        result = 31 * result + height
        result = 31 * result + keys.contentDeepHashCode()
        return result
    }
}

data class IrCommandSet(val frequency: Int, val codeMap: Map<String, IntArray>) {
    companion object {
        private fun buildCodeMap(
                codeFormat: String, codeMap: JSONObject): Map<String, IntArray> {
            val translator = CodeTranslator.getTranslator(codeFormat)

            return codeMap.toStringMap()
                    .map { (key, value) -> key to translator.buildCode(value) }
                    .toMap()
        }

        private fun JSONObject.toStringMap(): Map<String, String> {
            val map: MutableMap<String, String> = HashMap(length())
            keys().forEach { map[it] = getString(it) }
            return map
        }

        fun fromJson(obj: JSONObject) = IrCommandSet(
                obj.getInt("frequency"),
                buildCodeMap(obj.getString("codeFormat"), obj.getJSONObject("codeMap"))
        )
    }
}

data class RemoteDefinition(val layout: IrRemoteLayout, val commandDefs: IrCommandSet) {
    companion object {
        fun fromJson(remoteObj: JSONObject) = RemoteDefinition(
                IrRemoteLayout.fromJson(remoteObj.getJSONObject("layout")),
                IrCommandSet.fromJson(remoteObj.getJSONObject("irDef"))
        )
    }
}