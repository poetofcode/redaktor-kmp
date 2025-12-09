package specific

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun AsyncImage(
    modifier: Modifier,
    url: String,
    loadingView: @Composable () -> Unit,
    errorView: @Composable () -> Unit,
)