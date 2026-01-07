package specific

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import presentation.navigation.SetBackHandlerEffect
import presentation.navigation.SharedMemory


@Composable
actual fun BackHandler(
    cb: () -> Boolean,
) {
    LaunchedEffect(cb) {
        SharedMemory.effectFlow.emit(SetBackHandlerEffect(cb))
    }
}