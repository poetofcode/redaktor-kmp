package specific

import DesktopImageUtil
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

private val imageUtil = DesktopImageUtil()

@Composable
actual fun AsyncImage(
    modifier: Modifier,
    url: String,
    loadingView: @Composable () -> Unit,
    errorView: @Composable () -> Unit,
) {
    imageUtil.AsyncImage(url, modifier)
}