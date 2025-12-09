package specific

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.poetofcode.redaktor.AndroidImageUtil

private val imageUtil = AndroidImageUtil()

@Composable
actual fun AsyncImage(
    modifier: Modifier,
    url: String,
    loadingView: @Composable () -> Unit,
    errorView: @Composable () -> Unit,
) {
    imageUtil.AsyncImage(url, modifier)
}