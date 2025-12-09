package presentation.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import presentation.navigation.Effect
import presentation.navigation.LocalNavigators
import presentation.navigation.NavOptions
import presentation.navigation.NavigateBackEffect
import presentation.navigation.NavigateEffect
import presentation.navigation.NavigatorInfo
import presentation.navigation.NavigatorTag
import presentation.navigation.SetBackHandlerEffect
import presentation.navigation.SharedEvent
import presentation.navigation.SharedMemory
import presentation.navigation.SideEffect
import presentation.viewModelCoroutineScopeProvider

interface ViewModel<S> {
    fun onCleared()
}


abstract class BaseViewModel<S> : ViewModel<S> {

    val effectFlow: MutableSharedFlow<Effect> = MutableSharedFlow(
        extraBufferCapacity = 1
    )

    val sideEffectFlow = MutableSharedFlow<SideEffect>(
        extraBufferCapacity = 1,
    )

    val viewModelScope: CoroutineScope by lazy {
        viewModelCoroutineScopeProvider.scope
    }

    val state by lazy {
        mutableStateOf(onInitState())
    }

    fun reduce(cb: S.() -> S) {
        state.value = cb(state.value)
    }

    abstract fun onInitState(): S

    @Composable
    fun onViewReady() {
        SharedMemory.eventFlow
            .onEach { obtainSharedEvent(it) }
            .launchIn(viewModelScope)
    }

    protected open fun obtainSharedEvent(event: SharedEvent) {
        // Override it in derived classes
    }

    override fun onCleared() {
        // Override it in derived classes
    }

}

class EmptyViewModel : BaseViewModel<Unit>() {
    companion object {
        val INSTANCE = EmptyViewModel()
    }

    override fun onInitState() = Unit
}

@Composable
fun BaseViewModel<*>.collectEffects() {
    val navigators = LocalNavigators.current

    effectFlow.onEach { effect ->

        when (effect) {
            is NavigateBackEffect -> {
                val navigatorInfo = findNavigatorInfoByTag(navigators, effect.tag)
                navigatorInfo.navState.pop()
            }

            is NavigateEffect -> {
                val navigatorInfo = findNavigatorInfoByTag(navigators, effect.tag)
                when (effect.options) {
                    NavOptions.NONE -> navigatorInfo.navState.push(effect.screen)
                    NavOptions.REPLACE -> navigatorInfo.navState.moveToFront(effect.screen.screenId)
                    NavOptions.REPLACE_ALL -> {
                        // TODO
                        // navigatorInfo.navState.clearScreens()
                        // navigatorInfo.navState.push(effect.screen)
                    }
                }
            }

            is SetBackHandlerEffect -> {

            }
        }

    }.launchIn(viewModelScope)

}

fun BaseViewModel<*>.postEffect(effect: Effect) {
    viewModelScope.launch {
        effectFlow.emit(effect)
    }
}
fun BaseViewModel<*>.postSideEffect(effect: SideEffect) {
    viewModelScope.launch {
        sideEffectFlow.tryEmit(effect)
    }
}

fun BaseViewModel<*>.postSharedEvent(event: SharedEvent) {
    SharedMemory.eventFlow.tryEmit(event)
}

fun findNavigatorInfoByTag(navigators: List<NavigatorInfo>, tag: NavigatorTag): NavigatorInfo {
    val navigatorInfo = if (tag != NavigatorTag.CURRENT) {
        val navigatorType = navigators.firstOrNull { it.tag == tag }
        checkNotNull(navigatorType) { logErrorNotFound(tag.name) }
        navigatorType
    } else {
        val lastNavigatorType = navigators.lastOrNull()
        checkNotNull(lastNavigatorType) { logErrorNotFound(tag.name) }
        lastNavigatorType
    }
    return navigatorInfo
}

fun logErrorNotFound(arg: String) {
    println("Navigator with tag '${arg}' in not found'}")
}
