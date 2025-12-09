package presentation.navigation

import androidx.compose.runtime.Composable
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow


/* EFFECTS */

sealed interface Effect

data class NavigateEffect(
    val screen: BaseScreen<*>,
    val options: NavOptions = NavOptions.NONE,
    val tag: NavigatorTag = NavigatorTag.CURRENT,
) : Effect

data class NavigateBackEffect(
    val tag: NavigatorTag = NavigatorTag.CURRENT
) : Effect

data class SetBackHandlerEffect(
    val cb: () -> Boolean
) : Effect


/* SIDE EFFECTS */

interface SideEffect

data class ShowSnackErrorEffect(
    val text: String,
) : SideEffect

data class ShowModalBottomSheetEffect(
    val content: @Composable () -> Unit
) : SideEffect

object HideBottomSheetEffect : SideEffect

/* SHARED EVENTS */

interface SharedEvent

object SharedMemory {

    // TODO удалить потом
    val effectFlow: MutableSharedFlow<Effect> = MutableSharedFlow(
        extraBufferCapacity = 1
    )

    // Используется для обмена событиями между разными ViewModel'ями
    // Работа с ним происходит в базовом классе BaseViewModel
    val eventFlow = MutableSharedFlow<SharedEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.SUSPEND,
    )

} 