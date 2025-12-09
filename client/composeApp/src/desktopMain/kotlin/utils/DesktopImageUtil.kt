import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.skia.Image
import presentation.model.*
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.imageio.ImageIO

internal class DesktopImageUtil {

    private val cache = mutableMapOf<String, Resource<ImageBitmap>>()

    @Composable
    fun AsyncImage(url: String, modifier: Modifier) {
        val scope = rememberCoroutineScope()
        val imageBitmapState = remember { mutableStateOf<Resource<ImageBitmap>>(IdleResource) }

        Box(modifier = modifier) {
            when (val bitmap = imageBitmapState.value) {
                is CompleteResource -> {
                        Image(
                            bitmap = bitmap.result,
                            "",
                            modifier = Modifier.fillMaxSize()
                        )
                }

                IdleResource, LoadingResource -> {
                        Box(Modifier.padding(20.dp).fillMaxSize()) {
                            Text(text = "Загрузка", color = Color.Blue)
                        }
                }

                is ExceptionResource -> {
                    Box(Modifier.padding(20.dp).fillMaxSize()) {
                        Text(text = "Ошибка", color = Color.Red)
                    }
                }
            }
        }


        LaunchedEffect(Unit) {
            if (imageBitmapState.value is IdleResource) {
                val cached = cache.get(url)
                if (cached != null && cached !is IdleResource && cached !is ExceptionResource) {
                    imageBitmapState.value = cached
                    return@LaunchedEffect
                }

                scope.launch {
                    imageBitmapState.value = LoadingResource

                    imageBitmapState.value = try {
                        val imageRes = loadNetworkImage(url)
                        CompleteResource(imageRes)
                    } catch (e: Throwable) {
                        e.printStackTrace()
                        ExceptionResource(e)
                    }
                    cache.put(url, imageBitmapState.value)
                }
            }
        }

    }
}

internal suspend fun loadNetworkImage(link: String): ImageBitmap = withContext(Dispatchers.IO) {
    val url = URL(link.replace("/webp", "/png"))        // Fix for DTF (webp -> png format)
    val connection = url.openConnection() as HttpURLConnection
    connection.connect()

    val inputStream = connection.inputStream
    val bufferedImage = ImageIO.read(inputStream)

    val stream = ByteArrayOutputStream()
    ImageIO.write(bufferedImage, "png", stream)
    val byteArray = stream.toByteArray()

    return@withContext Image.makeFromEncoded(byteArray).asImageBitmap()
}