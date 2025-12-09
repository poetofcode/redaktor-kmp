package specific

import android.content.Context
import data.utils.ContentProvider
import java.io.File

class AndroidContentProvider constructor(
    val context: Context,
    val fileName: String,
) : ContentProvider {

    override fun provideContent(): String {
        val cachePath = File(context.cacheDir, "appcache")
        cachePath.mkdirs()
        val stream = File("$cachePath/$fileName").bufferedReader()
        return stream.use { it.readText() }
    }

    override fun saveContent(content: String) {
        val cachePath = File(context.cacheDir, "appcache")
        cachePath.mkdirs()
        val stream = File("$cachePath/$fileName")
        stream.printWriter().use {
            it.write(content)
        }
    }

}