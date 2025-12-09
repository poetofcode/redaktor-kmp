package specific

import androidx.compose.runtime.Composable

@Composable
expect fun BackHandler(
    cb: () -> Boolean,
)