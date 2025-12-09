package specific

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import presentation.navigation.SetBackHandlerEffect
import presentation.navigation.SharedMemory


@Composable
actual fun BackHandler(
    cb: () -> Boolean,
) {
    val scope = rememberCoroutineScope()
    scope.launch {
        SharedMemory.effectFlow.emit(SetBackHandlerEffect(cb))
    }
}