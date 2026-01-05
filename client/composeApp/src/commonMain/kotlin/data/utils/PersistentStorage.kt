package data.utils


import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.float
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.intOrNull
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.reflect.KProperty

/*
    Detecting the present annotations within the given object passed into a constructor
    https://stackoverflow.com/questions/4365095/detecting-the-present-annotations-within-the-given-object-passed-into-a-construc

    Serializer for class '...' is not found. Mark the class as @Serializable or provide the serializer explicitly
    https://stackoverflow.com/questions/71988144/serializer-for-class-is-not-found-mark-the-class-as-serializable-or-prov
 */

private val json = JsonProvider.json

@Serializer(forClass = ZonedDateTime::class)
object DateSerializer : KSerializer<ZonedDateTime> {
    private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    override fun serialize(encoder: Encoder, value: ZonedDateTime) {
        encoder.encodeString(value.format(formatter))
    }

    override fun deserialize(decoder: Decoder): ZonedDateTime {
        val str = decoder.decodeString()
        val instant = Instant.parse(str)
        val zonedDateTime = instant.atZone(ZoneId.systemDefault())
        return zonedDateTime
    }
}

interface ContentProvider {

    fun provideContent(): String

    fun saveContent(content: String)

}

class FileContentProvider(
    val fileName: String,
    val relativePath: String,
) : ContentProvider {
    override fun provideContent(): String {
        val cachePath = File("./", relativePath)
        cachePath.mkdirs()
        val stream = File("$cachePath/$fileName").bufferedReader()
        return try {
            stream.use { it.readText() }
        } catch (e: Throwable) {
            String()
        }
    }

    override fun saveContent(content: String) {
        val cachePath = File("./", relativePath)
        cachePath.mkdirs()
        val stream = File("$cachePath/$fileName")
        stream.printWriter().use {
            it.write(content)
        }
    }

}

class PersistentStorage(
    private val contentProvider: ContentProvider
) {
    private val _map: MutableMap<String, JsonElement> by lazy {
        val content = try {
            contentProvider.provideContent()
        } catch (e: Throwable) {
            e.printStackTrace()
            "{}"
        }

        val map = json.decodeFromString<Map<String, JsonElement>>(content)
        map.toMutableMap()
    }

    val map: Map<String, JsonElement> by lazy { _map }

    fun save(key: String, param: Any) {
        _map[key] = toJsonElement(param)
        val str = json.encodeToString(_map)
        contentProvider.saveContent(str)
    }

    fun deserializeAny(jsonElement: JsonElement): Any? {
        return when (jsonElement) {
            is JsonPrimitive -> when {
                jsonElement.booleanOrNull != null -> jsonElement.boolean
                jsonElement.intOrNull != null -> jsonElement.int
                jsonElement.floatOrNull != null -> jsonElement.float
                jsonElement.isString -> jsonElement.content
                else -> null
            }

            is JsonArray -> jsonElement.map { deserializeAny(it) }

            else -> null
        }
    }

    inline fun <reified T : Any> fetch(key: String): T? {
        val res = map.get(key) ?: return null
        return (deserializeAny(res) as? T)
    }

    private fun toJsonElement(param: Any): JsonElement {
        return when (param) {
            is String -> JsonPrimitive(param)
            is Int -> JsonPrimitive(param)
            is Float -> JsonPrimitive(param)
            is Boolean -> JsonPrimitive(param)

            is List<*> -> {
                val arrayContents = param.mapNotNull { listEntry ->
                    toJsonElement(listEntry!!)
                }
                JsonArray(arrayContents)
            }

            else -> JsonPrimitive(param.toString())
        }
    }
}


inline operator fun <reified T : Any> PersistentStorage.getValue(
    nothing: Any?,
    property: KProperty<*>
): T? {
    val propertyName = property.name
    return (fetch(propertyName) as? T)
}

inline operator fun <reified T : Any> PersistentStorage.setValue(
    nothing: Any?,
    property: KProperty<*>,
    value: T?
) {
    val propertyName = property.name
    value?.let {
        this.save(propertyName, value)
    }
}
