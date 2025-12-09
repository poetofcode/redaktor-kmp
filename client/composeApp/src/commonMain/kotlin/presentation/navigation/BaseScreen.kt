package presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import presentation.LocalMainAppState
import presentation.base.BaseViewModel
import presentation.base.ViewModel
import presentation.base.ViewModelStore
import presentation.base.collectEffects
import presentation.base.postSideEffect


interface Screen<T : ViewModel<*>> {
    val screenId: String

    val viewModel: T

    @Composable
    fun Content()
}


abstract class BaseScreen<T : BaseViewModel<*>> : Screen<T> {

    protected lateinit var viewModelStore: ViewModelStore

    override val screenId: String
        get() = this::class.java.typeName

    protected open val isMenuVisible: Boolean = false

    protected var isReady = false

    private val snackState = mutableStateOf<SnackState>(SnackState())
    private var snackHidingJob: Job? = null

    @Composable
    protected fun setMainMenuVisibility() {
        val appState = LocalMainAppState.current
        appState.isMenuVisible.value = isMenuVisible
    }

    fun setVMStore(viewModelStore: ViewModelStore) {
        this.viewModelStore = viewModelStore
    }

    @Composable
    fun PrepareContent() {
        val scope = rememberCoroutineScope()
        scope.launch {
            SharedMemory.effectFlow.emit(SetBackHandlerEffect { false })
        }
        setMainMenuVisibility()

        if (!isReady) {
            // Collecting effects by each viewModel
            (viewModel as? BaseViewModel<*>)?.run {
                onViewReady()
                collectEffects()
                collectSideEffects()
            }

            isReady = true
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Content()
            Snack(modifier = Modifier.align(Alignment.BottomCenter))
        }
    }

    @Composable
    fun Snack(modifier: Modifier = Modifier) {
        val state = snackState.value

        AnimatedVisibility(
            modifier = modifier,
            visible = state.isVisible
        ) {
            Box(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(Color.Red)
            ) {
                Text(
                    text = state.text, color = Color.White, modifier = Modifier.align(
                        Alignment.Center
                    )
                )
            }
        }
    }

    fun showSnack(state: SnackState, scope: CoroutineScope? = null) {
        snackState.value = state
        scope?.apply {
            if (!state.delayed) {
                snackHidingJob?.cancel()
                return@apply
            }
            snackHidingJob?.cancel()
            snackHidingJob = launch {
                delay(3000)
                snackState.value = snackState.value.copy(
                    isVisible = false
                )
            }
        }
    }

}

@Composable
fun BaseScreen<*>.collectSideEffects() {
    val localMainAppState = LocalMainAppState.current
    val scope = viewModel.viewModelScope

    viewModel.sideEffectFlow.onEach { effect ->
        when (effect) {
            is ShowSnackErrorEffect -> {
                showSnack(
                    state = SnackState(
                        text = effect.text,
                        isVisible = true,
                        delayed = true
                    ),
                    scope = scope
                )
            }

            is ShowModalBottomSheetEffect -> {
                localMainAppState.reduceBottomSheetState {
                    copy(
                        isVisible = true,
                        content = effect.content
                    )
                }
            }

            HideBottomSheetEffect -> {
                localMainAppState.reduceBottomSheetState {
                    copy(
                        isVisible = false
                    )
                }
            }
        }
    }.launchIn(scope)
}

// TODO предусмотреть абстрактный (или пустой открытый) метод для
//  обработки кастомный side-эффектов

fun BaseScreen<*>.postSideEffect(effect: SideEffect) {
    viewModel.postSideEffect(effect)
}

data class SnackState(
    val text: String = "",
    val isVisible: Boolean = false,
    val delayed: Boolean = false,
)